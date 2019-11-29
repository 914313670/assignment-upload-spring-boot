package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.entity.Busywork;
import top.liujingyanghui.assignmentupload.service.BusyworkService;
import top.liujingyanghui.assignmentupload.vo.Result;

import java.util.List;

/**
 * 作业控制器
 */
@RestController
@RequestMapping("api/busywork")
public class BusyworkController {

    @Autowired
    private BusyworkService busyworkService;

    /**
     * 分页查询作业
     * @param courseId
     * @param title
     * @param current
     * @param size
     * @return
     */
    @GetMapping("page")
    @PreAuthorize("hasRole('TEACHER')")
    public Result page(@RequestParam long courseId,@RequestParam String title,@RequestParam(defaultValue = "1") int current,
                       @RequestParam(defaultValue = "10") int size){
        Page<Busywork> busyworkPage = new Page<>(current, size);
        IPage<Busywork> page = busyworkService.page(busyworkPage, Wrappers.<Busywork>lambdaQuery().eq(Busywork::getCourseId, courseId).like(Busywork::getTitle, title));
        return Result.success(page);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    @PreAuthorize("hasRole('TEACHER')")
    public Result delete(@RequestParam long id){
        boolean remove = busyworkService.removeById(id);
        return remove?Result.success():Result.error("删除失败");
    }

    /**
     * 多选删除
     * @param ids
     * @return
     */
    @DeleteMapping("deletes")
    @PreAuthorize("hasRole('TEACHER')")
    public Result deletes(@RequestParam List<String> ids){
        boolean remove = busyworkService.removeByIds(ids);
        return remove?Result.success():Result.error("删除失败");
    }
}
