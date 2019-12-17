package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.config.UrlConfig;
import top.liujingyanghui.assignmentupload.entity.BusyworkUpload;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.BusyworkUploadService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 作业上传控制器
 */
@RestController
@RequestMapping("api/busywork")
public class BusyworkUploadController {
    @Autowired
    private UrlConfig urlConfig;
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private BusyworkUploadService busyworkUploadService;
    @Autowired
    private UserService userService;

    /**
     * 新增
     *
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Result add(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam long busyworkId) {
        if (file.isEmpty()) {
            return Result.error("上传失败");
        }
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long userId = JwtUtil.getSubject(token);
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        User user = userService.getById(userId);
        String newFileName = user.getNumber() + "_" + user.getName() + suffixName;
        StringBuilder tempName = new StringBuilder();
        tempName.append(urlConfig.getUploadBaseUrl()).append("busywork/info/").append(busyworkId).append("/").append(newFileName);
        String fielPath = tempName.toString();
        String url = urlConfig.getBaseUrl() + "resource/busywork/info/" + busyworkId + "/" + newFileName;
        try {
            BusyworkUpload busyworkUpload = busyworkUploadService.getOne(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getBusyworkId, busyworkId).
                    eq(BusyworkUpload::getUserId, userId));
            if (busyworkUpload == null) {
                File dest = new File(fielPath);
                // 判断路径是否存在，如果不存在则创建
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                busyworkUpload = new BusyworkUpload();
                // 保存文件
                file.transferTo(dest);

                busyworkUpload.setBusyworkId(busyworkId);
                busyworkUpload.setCreateTime(LocalDateTime.now());
                busyworkUpload.setUserId(userId);
                busyworkUpload.setUrl(url);
                busyworkUploadService.save(busyworkUpload);
            } else {
                File dest = new File(fielPath);
//                if (dest.exists()) {
//                    if (dest.delete()) {
                // 保存文件
                file.transferTo(dest);
                busyworkUpload.setCreateTime(LocalDateTime.now());
                busyworkUploadService.updateById(busyworkUpload);
//                    } else {
//                        return Result.error("上传失败");
//                    }
//                } else {
//                    return Result.error("上传失败");
//                }
            }
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error("上传失败");
    }
}
