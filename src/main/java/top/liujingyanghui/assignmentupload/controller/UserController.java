package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.ClassService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.PageVo;
import top.liujingyanghui.assignmentupload.vo.Result;
import top.liujingyanghui.assignmentupload.vo.StudentVo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassService classService;

    /**
     * 老师分页
     *
     * @param schoolId 学校id
     * @param email    邮箱
     * @param number   工号
     * @param name     姓名
     * @param current  当前页
     * @param size     每页大小
     * @return
     */
    @GetMapping("teacherPage")
    @PreAuthorize("hasRole('ADMIN')")
    public Result teacherPage(@RequestParam int schoolId, @RequestParam String email, @RequestParam String number, @RequestParam String name, @RequestParam(defaultValue = "1") int current,
                              @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = new Page<>(current, size);
        IPage<User> page = userService.page(userPage, Wrappers.<User>lambdaQuery().select(User::getId, User::getEmail, User::getName, User::getNumber, User::getCreateTime, User::getLastLoginTime)
                .eq(User::getRole, "ROLE_TEACHER").eq(User::getSchoolId, schoolId).like(User::getEmail, email).like(User::getNumber, number).like(User::getName, name));
        return Result.success(page);
    }

    /**
     * 学生分页
     *
     * @param schoolId 学校id
     * @param email    邮箱
     * @param number   工号
     * @param name     姓名
     * @param current  当前页
     * @param size     每页大小
     * @return
     */
    @GetMapping("studentPage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result studentPage(@RequestParam int schoolId, @RequestParam int classId, @RequestParam String email, @RequestParam String number, @RequestParam String name,
                              @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = new Page<>(current, size);
        IPage<User> page;
        if (-1 == classId) {
            page = userService.page(userPage, Wrappers.<User>lambdaQuery().select(User::getId, User::getEmail, User::getName, User::getNumber, User::getClassId, User::getCreateTime, User::getLastLoginTime)
                    .eq(User::getRole, "ROLE_STUDENT").eq(User::getSchoolId, schoolId).like(User::getEmail, email).like(User::getNumber, number).like(User::getName, name));
        } else {
            page = userService.page(userPage, Wrappers.<User>lambdaQuery().select(User::getId, User::getEmail, User::getName, User::getNumber, User::getClassId, User::getCreateTime, User::getLastLoginTime)
                    .eq(User::getRole, "ROLE_STUDENT").eq(User::getSchoolId, schoolId).eq(User::getClassId, classId).like(User::getEmail, email).like(User::getNumber, number).like(User::getName, name));
        }
        PageVo<StudentVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setSize(page.getSize());
        pageVo.setCurrent(page.getCurrent());
        ArrayList<StudentVo> studentVos = new ArrayList<>();
        Map<Long, Class> map = new HashMap<>();
        for (User item : page.getRecords()) {
            StudentVo studentVo = new StudentVo();
            BeanUtils.copyProperties(item, studentVo);
            Class clazz;
            if (item.getClassId() != null) {
                if (map.containsKey(item.getClassId())) {
                    clazz = map.get(item.getClassId());
                } else {
                    clazz = classService.getById(item.getClassId());
                    map.put(item.getClassId(), clazz);
                }
                studentVo.setClassName(clazz.getName());
                studentVo.setClassCreateName(clazz.getUserName());
                studentVos.add(studentVo);
            }
        }
        pageVo.setRecords(studentVos);
        return Result.success(pageVo);
    }

    /**
     * 老师信息修改
     *
     * @param user
     * @return
     */
    @PutMapping("teacherUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    public Result update(@RequestBody User user) {
        boolean update = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, user.getId()).set(User::getName, user.getName()).set(User::getNumber, user.getNumber())
                .set(User::getSchoolId, user.getSchoolId()));
        return update ? Result.success() : Result.error("修改失败");
    }

    /**
     * 从班级移除此学生
     *
     * @param request
     * @param id
     * @return
     */
    @DeleteMapping("remove")
    @PreAuthorize("hasRole('TEACHER')")
    public Result remove(HttpServletRequest request, @RequestParam Long id) {
        // 判断老师是否拥有修改权限
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long teacherId = JwtUtil.getSubject(token);
        User user = userService.getById(id);
        Class clazz = classService.getById(user.getClassId());
        if (!clazz.getUserId().equals(teacherId)) {
            return Result.error("你不是班级创建人，没有权限");
        }
        boolean update = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, id).set(User::getClassId, null));
        return update ? Result.success() : Result.error("移出失败");
    }
}
