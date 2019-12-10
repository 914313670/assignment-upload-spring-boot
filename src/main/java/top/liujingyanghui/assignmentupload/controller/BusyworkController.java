package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.config.UrlConfig;
import top.liujingyanghui.assignmentupload.entity.Busywork;
import top.liujingyanghui.assignmentupload.service.BusyworkService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 作业控制器
 */
@RestController
@RequestMapping("api/busywork")
public class BusyworkController {
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private UrlConfig urlConfig;
    @Autowired
    private BusyworkService busyworkService;

    /**
     * 作业新增
     * @return
     */
    @PostMapping("add")
    public Result add(@RequestBody Busywork busywork){
        busywork.setSubmitNum(0);
        busywork.setUnpaidNum(0);
        busywork.setCreateTime(LocalDateTime.now());
        boolean save = busyworkService.save(busywork);
        return save?Result.success():Result.error("新增失败");
    }

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

    /**
     * 附件上传
     * @param request
     * @param file
     * @return
     */
    @PostMapping("attachmentUpload")
    @PreAuthorize("hasRole('TEACHER')")
    public Result attachmentUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return Result.error("上传失败");
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        StringBuilder tempName = new StringBuilder();
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long teacherId = JwtUtil.getSubject(token);
        String newFileName=teacherId.toString()+"_"+sdf.format(new Date())+suffixName;
        tempName.append(urlConfig.getUploadBaseUrl()+"busywork/attachment/").append(newFileName);
        String fielPath = tempName.toString();
        File dest = new File(fielPath);
        // 判断路径是否存在，如果不存在则创建
        if(!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            // 保存文件
            file.transferTo(dest);
            return Result.success(urlConfig.getBaseUrl()+"resource/busywork/attachment/"+newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error("上传失败");
    }
}
