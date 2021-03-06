package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.entity.Course;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.ClassService;
import top.liujingyanghui.assignmentupload.service.CourseService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.Result;
import top.liujingyanghui.assignmentupload.vo.ResultEnum;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 班级控制器
 */
@RestController
@RequestMapping("api/class")
public class ClassController {

    @Autowired
    private TokenConfig tokenConfig;

    @Autowired
    private ClassService classService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    /**
     * 添加班级
     *
     * @param clazz 班级实体（班级名，年级，学校id必须传）
     * @return
     */
    @PostMapping("add")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result add(HttpServletRequest request, @RequestBody Class clazz) {
        if (StringUtils.isEmpty(clazz.getName()) || clazz.getSpecialty() == null || clazz.getSchoolId() == null) {
            return Result.error(ResultEnum.INPUT_IS_NULL);
        }
        List<Class> list = classService.list(Wrappers.<Class>lambdaQuery().eq(Class::getName, clazz.getName()).eq(Class::getSchoolId, clazz.getSchoolId()).
                eq(Class::getSpecialty, clazz.getSpecialty()));
        if (!list.isEmpty()) {
            return Result.error(ResultEnum.CLASS_IS_EXIST);
        }
        Long id = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        User user = userService.getById(id);
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUserId(id);
        clazz.setUserName(user.getName());
        clazz.setNumber(0);
        return classService.save(clazz) ? Result.success() : Result.error("添加失败");
    }

    /**
     * 根据学校获取专业列表
     *
     * @return
     */
    @GetMapping("getSpecialty")
    public Result getSpecialty(@RequestParam int schoolId) {
        List<Class> list = classService.list(Wrappers.<Class>lambdaQuery().select(Class::getSpecialty).eq(Class::getSchoolId, schoolId).groupBy(Class::getSpecialty));
        return Result.success(list);
    }

//    /**
//     * 根据老师获取班级
//     *
//     * @param teacherId
//     * @return
//     */
//    @GetMapping("getByTeacher")
//    @PreAuthorize("hasRole('TEACHER')")
//    public Result getByTeacher(@RequestParam int teacherId) {
//        List<Class> list = classService.list(Wrappers.<Class>lambdaQuery().select(Class::getId, Class::getName).eq(Class::getUserId, teacherId));
//        return Result.success(list);
//    }

    /**
     * 根据专业获取班级
     *
     * @return
     */
    @GetMapping("getBySpecialty")
    @PreAuthorize("hasRole('TEACHER')")
    public Result getBySpecialty(@RequestParam int schoolId, @RequestParam String specialty) {
        LambdaQueryWrapper<Class> queryWrapper;
        if (StringUtils.isEmpty(specialty)) {
            queryWrapper = Wrappers.<Class>lambdaQuery().select(Class::getName, Class::getId).eq(Class::getSchoolId, schoolId);
        } else {
            queryWrapper = Wrappers.<Class>lambdaQuery().select(Class::getName, Class::getId).eq(Class::getSchoolId, schoolId).eq(Class::getSpecialty, specialty);
        }
        List<Class> list = classService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 分页筛选查找班级数据
     *
     * @param schoolId  学校ID
     * @param specialty 年级
     * @param current   当前页
     * @param size      每页数量
     * @param nameKey   班级名称关键字
     * @return
     */
    @GetMapping("page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result page(@RequestParam int schoolId, @RequestParam String specialty, @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size,
                       @RequestParam String nameKey) {
        Page<Class> classPage = new Page<>(current, size);
        IPage<Class> page = classService.page(classPage, Wrappers.<Class>lambdaQuery().eq(Class::getSchoolId, schoolId).like(Class::getSpecialty, specialty)
                .like(Class::getName, nameKey));
        return Result.success(page);
    }

    /**
     * 修改学校名和年级
     *
     * @param clazz
     * @return
     */
    @PutMapping("update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result update(HttpServletRequest request, @RequestBody Class clazz) {
        Class one1 = classService.getOne(Wrappers.<Class>lambdaQuery().eq(Class::getName, clazz.getName()).eq(Class::getSpecialty, clazz.getSpecialty()).
                eq(Class::getSchoolId, clazz.getSchoolId()));
        if (one1 != null) {
            return Result.error("该班级名已存在");
        }
        // 判断老师是否拥有修改权限
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Claims claim = JwtUtil.getClaim(token);
        Long id = JwtUtil.getSubject(token);
        Class one = classService.getById(clazz.getId());
        if (claim.get("role").toString().equals("ROLE_TEACHER") && !one.getUserId().equals(id)) {
            return Result.error("你不是创建人，没有权限修改");
        }
        one.setName(clazz.getName());
        one.setSpecialty(clazz.getSpecialty());
        return classService.updateById(one) ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result delete(HttpServletRequest request, @RequestParam int id) {
        Claims claim = JwtUtil.getClaim(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        String role = claim.get("role").toString();
        Class clazz = classService.getById(id);
        if (clazz != null) {
            if (clazz.getNumber() > 0) {
                return Result.error("该班级下还有学生不能删除");
            }
            if (role.equals("ROLE_TEACHER") && !clazz.getUserId().equals(userId)) {
                return Result.error("没有相应权限");
            }
        } else {
            return Result.error("删除失败");
        }
        int count = courseService.count(Wrappers.<Course>lambdaQuery().eq(Course::getClassId, id));
        if (count > 0) {
            return Result.error("该班级下含有课程");
        }
        boolean remove = classService.removeById(id);
        return remove ? Result.success() : Result.error("删除失败");
    }

    /**
     * 多选删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deletes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result deletes(HttpServletRequest request, @RequestParam List<String> ids) {
        Claims claim = JwtUtil.getClaim(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        String role = claim.get("role").toString();
        Collection<Class> classes = classService.listByIds(ids);
        for (Class item : classes) {
            if (item.getNumber() > 0) {
                return Result.error("所选班级下还有学生");
            }
            if (role.equals("ROLE_TEACHER") && !item.getUserId().equals(userId)) {
                return Result.error("所选班级没有相应权限");
            }
        }
        for (String id : ids) {
            int count = courseService.count(Wrappers.<Course>lambdaQuery().eq(Course::getClassId, id));
            if (count > 0) {
                return Result.error("该班级下含有课程");
            }
        }
        boolean delete = classService.removeByIds(ids);
        return delete ? Result.success() : Result.error("删除失败");
    }

    /**
     * 根据学校id获取班级
     *
     * @param schoolId 学校id
     * @return
     */
    @GetMapping("getBySchool")
    public Result getBySchool(@RequestParam int schoolId) {
        List<Class> list = classService.list(Wrappers.<Class>lambdaQuery().select(Class::getName, Class::getId).eq(Class::getSchoolId, schoolId));
        return Result.success(list);
    }

    /**
     * 根据学校获取班级数量（学校id为-1时获取所有学校）
     *
     * @return
     */
    @GetMapping("count")
    public Result count(@RequestParam int schoolId) {
        return Result.success(classService.count(Wrappers.<Class>lambdaQuery().eq(schoolId != -1, Class::getSchoolId, schoolId)));
    }
}
