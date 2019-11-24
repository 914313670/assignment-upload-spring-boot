package top.liujingyanghui.assignmentupload.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.liujingyanghui.assignmentupload.dao.CourseMapper;
import top.liujingyanghui.assignmentupload.entity.Course;
import top.liujingyanghui.assignmentupload.service.CourseService;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
}
