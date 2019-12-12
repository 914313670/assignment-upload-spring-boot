package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
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
import top.liujingyanghui.assignmentupload.vo.CourseVo;
import top.liujingyanghui.assignmentupload.vo.PageVo;
import top.liujingyanghui.assignmentupload.vo.Result;
import top.liujingyanghui.assignmentupload.vo.ResultEnum;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("api/course")
public class CourseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClassService classService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TokenConfig tokenConfig;

    /**
     * 新增课程
     *
     * @param request
     * @param course
     * @return
     */
    @PostMapping("add")
    @PreAuthorize("hasRole('TEACHER')")
    public Result add(HttpServletRequest request, @RequestBody Course course) {
        Course one = courseService.getOne(Wrappers.<Course>lambdaQuery().eq(Course::getName, course.getName()).eq(Course::getClassId, course.getClassId()));
        if (one != null) {
            return Result.error("该课程已存在");
        }
        Long id = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        User teacher = userService.getById(id);
        course.setSchoolId(teacher.getSchoolId());
        course.setTeacherId(teacher.getId());
        course.setTeacherName(teacher.getName());
        course.setBusyworkNum(0);
        course.setCreateTime(LocalDateTime.now());
        boolean save = courseService.save(course);
        return save ? Result.success() : Result.error("新增失败");
    }

    /**
     * 课程分页查询
     *
     * @param schoolId    学校id
     * @param classId     班级id
     * @param teacherName 老师姓名
     * @param teacherId   老师id
     * @param name        课程名
     * @param current     当前页
     * @param size        每页大小
     * @return
     */
    @GetMapping("page")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result page(@RequestParam int schoolId, @RequestParam int classId, @RequestParam(required = false) String teacherName,
                       @RequestParam(required = false) Long teacherId, @RequestParam String name, @RequestParam(defaultValue = "1") int current,
                       @RequestParam(defaultValue = "10") int size) {
        Page<Course> coursePage = new Page<>(current, size);
        LambdaQueryWrapper<Course> queryWrapper;
        if (-1 == classId) {
            // 判断是根据老师Id还是老师姓名查询
            if (teacherId != null) {
                queryWrapper = Wrappers.<Course>lambdaQuery().like(Course::getName, name).eq(Course::getSchoolId, schoolId).eq(Course::getTeacherId, teacherId).
                        like(Course::getName, name);
            } else {
                queryWrapper = Wrappers.<Course>lambdaQuery().like(Course::getName, name).eq(Course::getSchoolId, schoolId).like(Course::getTeacherName, teacherName).
                        like(Course::getName, name);
            }
        } else {
            // 判断是根据老师Id还是老师姓名查询
            if (teacherId != null) {
                queryWrapper = Wrappers.<Course>lambdaQuery().eq(Course::getClassId, classId).eq(Course::getTeacherId, teacherId).
                        like(Course::getName, name).like(Course::getName, name);
            } else {
                queryWrapper = Wrappers.<Course>lambdaQuery().eq(Course::getClassId, classId).like(Course::getTeacherName, teacherName).
                        like(Course::getName, name).like(Course::getName, name);
            }
        }
        IPage<Course> page = courseService.page(coursePage, queryWrapper);
        PageVo<CourseVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setSize(page.getSize());
        pageVo.setCurrent(page.getCurrent());
        ArrayList<CourseVo> list = new ArrayList<>();
        Map<Long, Class> classMap = new HashMap<>();
        for (Course item : page.getRecords()) {
            CourseVo courseVo = new CourseVo();
            BeanUtils.copyProperties(item, courseVo);
            Class clazz;
            if (classMap.containsKey(item.getClassId())) {
                clazz = classMap.get(item.getClassId());
            } else {
                clazz = classService.getById(item.getClassId());
                classMap.put(item.getClassId(), clazz);
            }
            courseVo.setClassName(clazz.getName());
            list.add(courseVo);
        }
        pageVo.setRecords(list);
        return Result.success(pageVo);
    }

    /**
     * 修改课程名
     *
     * @param course
     * @return
     */
    @PutMapping("update")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result update(@RequestBody Course course) {
        boolean update = courseService.update(Wrappers.<Course>lambdaUpdate().eq(Course::getId, course.getId()).set(Course::getName, course.getName()).set(Course::getClassId, course.getClassId()));
        return update ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除课程
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result delete(@RequestParam Long id) {
        Course course = courseService.getById(id);
        if (course.getBusyworkNum() > 0) {
            return Result.error("该课程下有作业，不能删除");
        }
        boolean del = courseService.removeById(id);
        return del ? Result.success() : Result.error("删除失败");
    }


    /**
     * 多选删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deletes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result deletes(@RequestParam List<String> ids) {
        // 预留是否删除
        Collection<Course> courses = courseService.listByIds(ids);
        for (Course course : courses) {
            if (course.getBusyworkNum() > 0) {
                return Result.error("该课程下有作业，不能删除");
            }
        }
        boolean delete = courseService.removeByIds(ids);
        return delete ? Result.success() : Result.error("删除失败");
    }

    /**
     * 根据班级查询课程
     * @param classId
     * @return
     */
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("getByClass")
    public Result getByClass(HttpServletRequest request,@RequestParam int classId){
        Long id = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        List<Course> list = courseService.list(Wrappers.<Course>lambdaQuery().eq(Course::getClassId, classId).eq(Course::getTeacherId,id));
        return Result.success(list);
    }

    /**
     * 学生获取课程列表
     * @param request
     * @return
     */
    @GetMapping("getByStudent")
    @PreAuthorize("hasRole('STUDENT')")
    public Result getByStudent(HttpServletRequest request){
        Long id = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        User user = userService.getById(id);
        if (user.getClassId()==null){
            return Result.success(ResultEnum.NOT_JOIN_CLASS);
        }
        List<Course> list = courseService.list(Wrappers.<Course>lambdaQuery().eq(Course::getClassId, user.getClassId()));
        return Result.success(list);
    }

}
