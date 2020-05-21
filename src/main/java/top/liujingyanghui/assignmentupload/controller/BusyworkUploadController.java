package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.config.UrlConfig;
import top.liujingyanghui.assignmentupload.entity.Busywork;
import top.liujingyanghui.assignmentupload.entity.BusyworkUpload;
import top.liujingyanghui.assignmentupload.entity.Course;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.BusyworkService;
import top.liujingyanghui.assignmentupload.service.BusyworkUploadService;
import top.liujingyanghui.assignmentupload.service.CourseService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.BusyworkRecordVo;
import top.liujingyanghui.assignmentupload.vo.PageVo;
import top.liujingyanghui.assignmentupload.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 作业上传控制器
 */
@RestController
@RequestMapping("api/busywork-upload")
public class BusyworkUploadController {
    @Autowired
    private UrlConfig urlConfig;
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private BusyworkUploadService busyworkUploadService;
    @Autowired
    private UserService userService;
    @Autowired
    private BusyworkService busyworkService;
    @Autowired
    private CourseService courseService;

    /**
     * 新增
     *
     * @return
     */
    @PostMapping("add")
    @PreAuthorize("hasRole('STUDENT')")
    public Result add(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam long busyworkId) {
        if (file.isEmpty()) {
            System.out.println("1");
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
        File dest = new File(fielPath);
        try {
            BusyworkUpload busyworkUpload = busyworkUploadService.getOne(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getBusyworkId, busyworkId).
                    eq(BusyworkUpload::getUserId, userId));
            if (busyworkUpload == null) {
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
                busyworkUpload.setClassId(user.getClassId());
                busyworkUploadService.save(busyworkUpload);
            } else {
                StringBuilder delPath = new StringBuilder();
                String delUrl = busyworkUpload.getUrl();
                String delsuffix = delUrl.substring(delUrl.lastIndexOf("."));
                delPath.append(urlConfig.getUploadBaseUrl()).append("busywork/info/").append(busyworkId).append("/").append(user.getNumber()).append("_").append(user.getName()).
                        append(delsuffix);
                File del = new File(delPath.toString());
                if (del.exists()) {
                    if (del.delete()) {
                        // 保存文件
                        file.transferTo(dest);
                        busyworkUpload.setCreateTime(LocalDateTime.now());
                        busyworkUpload.setUrl(url);
                        busyworkUploadService.updateById(busyworkUpload);
                    } else {
                        return Result.error("上传失败");
                    }
                } else {
                    return Result.error("上传失败");
                }
            }
            return Result.success(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error("上传失败");
    }

    /**
     * 文件删除
     *
     * @param request
     * @param url
     * @return
     */
    @DeleteMapping("delete")
    @PreAuthorize("hasRole('STUDENT')")
    public Result delete(HttpServletRequest request, @RequestParam String url, @RequestParam long busyworkId) {
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        long userId = JwtUtil.getSubject(token);
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String[] split = fileName.split("_");
        String username = split[0]; // 学号
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, username));
        if (userId != user.getId()) {
            return Result.error("删除失败");
        }
        StringBuilder path = new StringBuilder();
        path.append(urlConfig.getUploadBaseUrl()).append("busywork/info/").append(busyworkId).append("/").append(fileName);
        File file = new File(path.toString());
        if (file.exists()) {
            if (file.delete()) {
                busyworkUploadService.remove(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getUserId, user.getId()).eq(BusyworkUpload::getBusyworkId, busyworkId));
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 获取作业提交记录
     *
     * @param busyworkId
     * @return
     */
    @GetMapping("getRecord")
    public Result getRecord(@RequestParam long busyworkId, @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        Busywork busywork = busyworkService.getById(busyworkId);
        Course course = courseService.getById(busywork.getCourseId());
        Page<User> page = new Page<>(current, size);
        System.out.println(course.getClassId());
        IPage<User> iPage = userService.page(page, Wrappers.<User>lambdaQuery().eq(User::getClassId, course.getClassId()).eq(User::getRole, "ROLE_STUDENT").
                orderByAsc(User::getNumber));
        List<BusyworkUpload> busyworkUploadList = busyworkUploadService.list(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getClassId, course.getClassId()).
                eq(BusyworkUpload::getBusyworkId, busyworkId));
        PageVo<BusyworkRecordVo> pageVo = new PageVo<>();
        pageVo.setCurrent(iPage.getCurrent());
        pageVo.setSize(iPage.getSize());
        pageVo.setTotal(iPage.getTotal());
        ArrayList<BusyworkRecordVo> list = new ArrayList<>();
        for (User user : iPage.getRecords()) {
            BusyworkRecordVo busyworkRecordVo = new BusyworkRecordVo();
            busyworkRecordVo.setName(user.getName());
            busyworkRecordVo.setNumber(user.getNumber());
            for (BusyworkUpload busyworkUpload : busyworkUploadList) {
                if (user.getId().equals(busyworkUpload.getUserId())) {
                    busyworkRecordVo.setCreateTime(busywork.getCreateTime());
                    busyworkRecordVo.setStatus(1);
                    busyworkRecordVo.setUrl(busyworkUpload.getUrl());
                    break;
                }
            }
            if (busyworkRecordVo.getStatus() == null) {
                busyworkRecordVo.setStatus(2);
            }
            list.add(busyworkRecordVo);
        }
        pageVo.setRecords(list);
        return Result.success(pageVo);
    }
}
