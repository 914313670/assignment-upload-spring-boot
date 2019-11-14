package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.service.ClassService;
import top.liujingyanghui.assignmentupload.vo.Result;

import java.util.List;

/**
 * 班级控制器
 */
@RestController
@RequestMapping("api/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 根据学校获取年级列表
     *
     * @return
     */
    @GetMapping("getGrade")
    public Result getGrade(@RequestParam int schoolId) {
        List<Class> list = classService.list(Wrappers.<Class>lambdaQuery().select(Class::getGrade).eq(Class::getSchoolId,
                schoolId).groupBy(Class::getGrade).orderByDesc(Class::getGrade));
        return Result.success(list);
    }

    /**
     * 分页筛选查找班级数据
     *
     * @param schoolId 学校ID
     * @param grade    年级
     * @param current  当前页
     * @param size     每页数量
     * @param nameKey  班级名称关键字
     * @return
     */
    @GetMapping("page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result page(@RequestParam int schoolId, @RequestParam int grade, @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size,
                       @RequestParam String nameKey) {
        Page<Class> classPage = new Page<>(current, size);
        IPage<Class> page = null;
        if (-1 == grade) {
            page = classService.page(classPage, Wrappers.<Class>lambdaQuery().eq(Class::getSchoolId, schoolId).like(Class::getName, nameKey));
        } else {
            page = classService.page(classPage, Wrappers.<Class>lambdaQuery().eq(Class::getSchoolId, schoolId).eq(Class::getGrade, grade)
                    .like(Class::getName, nameKey));
        }
        return Result.success(page);
    }
}
