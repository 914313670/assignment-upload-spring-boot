package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.entity.ModifyClassApply;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.ClassService;
import top.liujingyanghui.assignmentupload.service.ModifyClassApplyService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 转班申请控制器
 *
 * @author wdh
 * @date 2019/12/31 16:50
 */
@RestController
@RequestMapping("api/modifyClassApply")
public class ModifyClassApplyController {
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private ModifyClassApplyService modifyClassApplyService;
    @Autowired
    private ClassService classService;
    @Autowired
    private UserService userService;

    /**
     * 待审核数量
     * @param request
     * @return
     */
    @GetMapping("count")
    public Result count(HttpServletRequest request){
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        int count = modifyClassApplyService.count(Wrappers.<ModifyClassApply>lambdaQuery().eq(ModifyClassApply::getToUserId, userId).isNull(ModifyClassApply::getStatus));
        return Result.success(count);
    }

    /**
     * 申请新增
     *
     * @param request
     * @param modifyClassApply
     * @return
     */
    @PostMapping("add")
    public Result add(HttpServletRequest request, @RequestBody ModifyClassApply modifyClassApply) {
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        ModifyClassApply one = modifyClassApplyService.getOne(Wrappers.<ModifyClassApply>lambdaQuery().eq(ModifyClassApply::getUserId, userId).isNull(ModifyClassApply::getStatus));
        if (one != null) {
            return Result.error("请勿重复申请！");
        }
        Class toClass = classService.getById(modifyClassApply.getToClassId());
        User user = userService.getById(userId);
        if (user.getClassId() != null) {
            Class fromClass = classService.getById(user.getClassId());
            modifyClassApply.setFromClassName(fromClass.getName());
            modifyClassApply.setFromClassId(user.getClassId());
        }
        modifyClassApply.setId(null);
        modifyClassApply.setHandleTime(null);
        modifyClassApply.setStatus(null);
        modifyClassApply.setUserId(userId);
        modifyClassApply.setUserName(user.getName());
        modifyClassApply.setCreateTime(LocalDateTime.now());
        modifyClassApply.setToUserId(toClass.getUserId());
        modifyClassApply.setToClassName(toClass.getName());
        boolean save = modifyClassApplyService.save(modifyClassApply);
        return save ? Result.success() : Result.error();
    }

    /**
     * 审核列表
     *
     * @return
     */
    @GetMapping("page")
    public Result page(HttpServletRequest request, @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        Page<ModifyClassApply> modifyClassApplyPage = new Page<>(current, size);
        IPage<ModifyClassApply> page = modifyClassApplyService.page(modifyClassApplyPage, Wrappers.<ModifyClassApply>lambdaQuery().eq(ModifyClassApply::getToUserId, userId).isNull(ModifyClassApply::getStatus).
                orderByDesc(ModifyClassApply::getCreateTime));
        return Result.success(page);
    }

    /**
     * 同意申请
     *
     * @param request
     * @param id
     * @return
     */
    @GetMapping("agree")
    public Result agree(HttpServletRequest request, @RequestParam long id) {
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        return modifyClassApplyService.agree(userId, id) ? Result.success() : Result.error("操作失败！");
    }

    /**
     * 反对申请
     *
     * @param request
     * @param id
     * @return
     */
    @GetMapping("oppose")
    public Result oppose(HttpServletRequest request, @RequestParam long id) {
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        ModifyClassApply modifyClassApply = modifyClassApplyService.getById(id);
        if (!userId.equals(modifyClassApply.getToUserId())) {
            return Result.error("你没有权限");
        }
        modifyClassApplyService.update(Wrappers.<ModifyClassApply>lambdaUpdate().eq(ModifyClassApply::getId, modifyClassApply.getId()).set(ModifyClassApply::getStatus, 0).
                set(ModifyClassApply::getHandleTime, LocalDateTime.now()));
        return Result.success();
    }

    /**
     * 批量同意申请
     * @param request
     * @param ids
     * @return
     */
    @GetMapping("batch-agree")
    public Result batchAgree(HttpServletRequest request, @RequestParam List<String> ids) {
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        return modifyClassApplyService.batchAgree(userId, ids) ? Result.success() : Result.error("操作失败！");
    }

    /**
     * 批量拒绝申请
     * @param request
     * @param ids
     * @return
     */
    @GetMapping("batch-oppose")
    public Result batchOppose(HttpServletRequest request, @RequestParam List<String> ids) {
        Long userId = JwtUtil.getSubject(request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length()));
        return modifyClassApplyService.batchOppose(userId, ids) ? Result.success() : Result.error("操作失败！");
    }
}
