package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.entity.Course;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.ClassService;
import top.liujingyanghui.assignmentupload.service.CourseService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.vo.CourseVo;
import top.liujingyanghui.assignmentupload.vo.PageVo;
import top.liujingyanghui.assignmentupload.vo.Result;
import top.liujingyanghui.assignmentupload.vo.StudentVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result page(@RequestParam int schoolId, @RequestParam int classId, @RequestParam String teacherName, @RequestParam String name, @RequestParam(defaultValue = "1") int current,
                       @RequestParam(defaultValue = "10") int size) {
        Page<Course> coursePage = new Page<>(current, size);
        IPage<Course> page;
        if (-1 == classId) {
            page = courseService.page(coursePage, Wrappers.<Course>lambdaQuery().like(Course::getName, name).eq(Course::getSchoolId, schoolId).like(Course::getTeacherName, teacherName).
                    like(Course::getName, name));
        } else {
            page = courseService.page(coursePage, Wrappers.<Course>lambdaQuery().like(Course::getName, name).eq(Course::getClassId, classId).like(Course::getTeacherName, teacherName).
                    like(Course::getName, name));
        }
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
}
