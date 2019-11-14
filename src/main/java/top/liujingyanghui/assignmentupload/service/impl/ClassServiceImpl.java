package top.liujingyanghui.assignmentupload.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.dao.ClassMapper;
import top.liujingyanghui.assignmentupload.service.ClassService;
@Service
public class ClassServiceImpl extends ServiceImpl<ClassMapper, Class> implements ClassService{

}
