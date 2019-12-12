package top.liujingyanghui.assignmentupload.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import top.liujingyanghui.assignmentupload.dao.BusyworkMapper;
import top.liujingyanghui.assignmentupload.dao.CourseMapper;
import top.liujingyanghui.assignmentupload.entity.Busywork;
import top.liujingyanghui.assignmentupload.entity.Course;
import top.liujingyanghui.assignmentupload.exception.MyException;
import top.liujingyanghui.assignmentupload.service.BusyworkService;
import top.liujingyanghui.assignmentupload.vo.ResultEnum;

import java.time.LocalDateTime;

@Service
@Transactional
public class BusyworkServiceImpl extends ServiceImpl<BusyworkMapper, Busywork> implements BusyworkService{
    @Autowired
    private BusyworkMapper busyworkMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public void add(Busywork busywork) {
        busywork.setSubmitNum(0);
        busywork.setUnpaidNum(0);
        busywork.setCreateTime(LocalDateTime.now());
        int insert = busyworkMapper.insert(busywork);
        if (insert<1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MyException(ResultEnum.ERROR);
        }
        Course course = courseMapper.selectById(busywork.getCourseId());
        int update = courseMapper.update(null, Wrappers.<Course>lambdaUpdate().eq(Course::getId, course.getId()).set(Course::getBusyworkNum,
                course.getBusyworkNum() + 1));
        if (update<1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new MyException(ResultEnum.ERROR);
        }
    }
}
