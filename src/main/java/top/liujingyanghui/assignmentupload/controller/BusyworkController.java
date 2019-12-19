package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.config.UrlConfig;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.entity.*;
import top.liujingyanghui.assignmentupload.exception.MyException;
import top.liujingyanghui.assignmentupload.service.*;
import top.liujingyanghui.assignmentupload.utils.FilePackageUtil;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;
    @Autowired
    private BusyworkUploadService busyworkUploadService;
    @Autowired
    private ClassService classService;
    @Autowired
    private FilePackageUtil filePackageUtil;

    /**
     * 作业新增
     *
     * @return
     */
    @PostMapping("add")
    @PreAuthorize("hasRole('TEACHER')")
    public Result add(@RequestBody Busywork busywork) {
        busyworkService.add(busywork);
        return Result.success();
    }

    /**
     * 更新
     *
     * @param busywork
     * @return
     */
    @PutMapping("update")
    @PreAuthorize("hasRole('TEACHER')")
    public Result update(@RequestBody Busywork busywork) {
        busywork.setCreateTime(null);
        busywork.setSubmitNum(null);
        busywork.setUnpaidNum(null);
        boolean update = busyworkService.updateById(busywork);
        return update ? Result.success() : Result.error();
    }

    /**
     * 根据ID查询单个
     *
     * @return
     */
    @GetMapping("getOne")
    public Result getOne(@RequestParam long id) {
        Busywork busywork = busyworkService.getById(id);
        BusyworkVo busyworkVo = new BusyworkVo();
        if (busywork != null) {
            BeanUtils.copyProperties(busywork, busyworkVo);
            Course course = courseService.getById(busywork.getCourseId());
            busyworkVo.setClassId(course.getClassId());
            busyworkVo.setCourseName(course.getName());
        } else {
            return Result.error("没有该作业");
        }
        return Result.success(busyworkVo);
    }

    /**
     * 学生获取详情
     *
     * @return
     */
    @GetMapping("studentGetDetails")
    @PreAuthorize("hasRole('STUDENT')")
    public Result studentGetDetails(HttpServletRequest request, @RequestParam long id) {
        Busywork busywork = busyworkService.getById(id);
        BusyworkStudentDeatilsVo busyworkStudentDeatilsVo = new BusyworkStudentDeatilsVo();
        if (busywork != null) {
            BeanUtils.copyProperties(busywork, busyworkStudentDeatilsVo);
            Course course = courseService.getById(busywork.getCourseId());
            busyworkStudentDeatilsVo.setCourseName(course.getName());
            String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
            Long userId = JwtUtil.getSubject(token);
            BusyworkUpload busyworkUpload = busyworkUploadService.getOne(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getUserId, userId).eq(BusyworkUpload::getBusyworkId, id));
            if (busyworkUpload == null) {
                if (LocalDateTime.now().isBefore(busywork.getEndTime())) {
                    busyworkStudentDeatilsVo.setStatus(3);
                } else {
                    busyworkStudentDeatilsVo.setStatus(4);
                }
            } else {
                busyworkStudentDeatilsVo.setUrl(busyworkUpload.getUrl());
                if (LocalDateTime.now().isBefore(busywork.getEndTime())) {
                    busyworkStudentDeatilsVo.setStatus(1);
                } else {
                    busyworkStudentDeatilsVo.setStatus(2);
                }
            }

        } else {
            return Result.error("没有该作业");
        }
        return Result.success(busyworkStudentDeatilsVo);
    }

    /**
     * 分页查询作业
     *
     * @param courseId
     * @param title
     * @param current
     * @param size
     * @return
     */
    @GetMapping("page")
    @PreAuthorize("hasRole('TEACHER')")
    public Result page(@RequestParam long courseId, @RequestParam String title, @RequestParam(defaultValue = "1") int current,
                       @RequestParam(defaultValue = "10") int size) {
        Page<Busywork> busyworkPage = new Page<>(current, size);
        IPage<Busywork> page = busyworkService.page(busyworkPage, Wrappers.<Busywork>lambdaQuery().eq(Busywork::getCourseId, courseId).like(Busywork::getTitle, title).
                orderByDesc(Busywork::getCreateTime));
        PageVo<Busywork> pageVo = new PageVo<>();
        Course course = courseService.getById(courseId);
        Class clazz = classService.getById(course.getClassId());
        pageVo.setTotal(page.getTotal());
        pageVo.setSize(page.getSize());
        pageVo.setCurrent(page.getCurrent());
        ArrayList<Busywork> busyworks = new ArrayList<>();
        for (Busywork item : page.getRecords()) {
            int submitCount = busyworkUploadService.count(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getBusyworkId, item.getId()));
            item.setUnpaidNum(clazz.getNumber() - submitCount);
            item.setSubmitNum(submitCount);
            busyworks.add(item);
        }
        pageVo.setRecords(busyworks);
        return Result.success(pageVo);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    @PreAuthorize("hasRole('TEACHER')")
    public Result delete(@RequestParam long id) {
        boolean remove = busyworkService.removeById(id);
        return remove ? Result.success() : Result.error("删除失败");
    }

    /**
     * 多选删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deletes")
    @PreAuthorize("hasRole('TEACHER')")
    public Result deletes(@RequestParam List<String> ids) {
        boolean remove = busyworkService.removeByIds(ids);
        return remove ? Result.success() : Result.error("删除失败");
    }

    /**
     * 附件上传
     *
     * @param request
     * @param file
     * @return
     */
    @PostMapping("attachmentUpload")
    @PreAuthorize("hasRole('TEACHER')")
    public Result attachmentUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传失败");
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        StringBuilder tempName = new StringBuilder();
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long teacherId = JwtUtil.getSubject(token);
        String newFileName = teacherId.toString() + "_" + sdf.format(new Date()) + suffixName;
        tempName.append(urlConfig.getUploadBaseUrl() + "busywork/attachment/").append(newFileName);
        String fielPath = tempName.toString();
        File dest = new File(fielPath);
        // 判断路径是否存在，如果不存在则创建
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            // 保存文件
            file.transferTo(dest);
            return Result.success(urlConfig.getBaseUrl() + "resource/busywork/attachment/" + newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error("上传失败");
    }

    /**
     * 学生分页
     *
     * @return
     */
    @GetMapping("studentPage")
    public Result studentPage(HttpServletRequest request, @RequestParam long courseId, @RequestParam String title, @RequestParam(defaultValue = "1") int current,
                              @RequestParam(defaultValue = "10") int size) {
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long userId = JwtUtil.getSubject(token);
        User user = userService.getById(userId);
        Course course = courseService.getById(courseId);
        if (!course.getClassId().equals(user.getClassId())) {
            return Result.error("你没有访问该课程的权限");
        }
        Page<Busywork> busyworkPage = new Page<>(current, size);
        IPage<Busywork> page = busyworkService.page(busyworkPage, Wrappers.<Busywork>lambdaQuery().eq(Busywork::getCourseId, courseId).like(Busywork::getTitle, title).
                orderByDesc(Busywork::getCreateTime));
        PageVo<BusyworkStudentPageVo> pageVo = new PageVo<>();
        pageVo.setCurrent(current);
        pageVo.setSize(size);
        pageVo.setTotal(page.getTotal());
        Class clazz = classService.getById(course.getClassId());
        List<BusyworkStudentPageVo> list = new ArrayList<>();
        List<Busywork> busyworks = page.getRecords();
        for (Busywork busywork : busyworks) {
            BusyworkStudentPageVo busyworkStudentPageVo = new BusyworkStudentPageVo();
            BeanUtils.copyProperties(busywork, busyworkStudentPageVo);
            BusyworkUpload busyworkUpload = busyworkUploadService.getOne(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getUserId, userId).
                    eq(BusyworkUpload::getBusyworkId, busywork.getId()));
            if (busyworkUpload == null) {
                if (LocalDateTime.now().isBefore(busywork.getEndTime())) {
                    busyworkStudentPageVo.setStatus(3);
                } else {
                    busyworkStudentPageVo.setStatus(4);
                }
            } else {
                if (LocalDateTime.now().isBefore(busywork.getEndTime())) {
                    busyworkStudentPageVo.setStatus(1);
                } else {
                    busyworkStudentPageVo.setStatus(2);
                }
            }
            int submitCount = busyworkUploadService.count(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getBusyworkId, busywork.getId()));
            busyworkStudentPageVo.setUnpaidNum(clazz.getNumber() - submitCount);
            busyworkStudentPageVo.setSubmitNum(submitCount);
            list.add(busyworkStudentPageVo);
        }
        pageVo.setRecords(list);
        return Result.success(pageVo);
    }

    /**
     * 学生作业打包下载
     *
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("package-download")
    public String downloadFile(@RequestParam String token, HttpServletResponse response, @RequestParam long busyworkId) throws Exception {
        Long userId = JwtUtil.getSubject(token);
        Busywork busywork = busyworkService.getById(busyworkId);
        Course course = courseService.getById(busywork.getCourseId());
        if (!userId.equals(course.getTeacherId())) {
            throw new MyException(ResultEnum.ERROR);
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        filePackageUtil.downLoadFiles("busywork/info/" + busyworkId, busywork.getTitle() + "_" + df.format(new Date()) + ".zip", response);
        return null;
    }
}
