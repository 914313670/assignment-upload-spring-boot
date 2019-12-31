package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        modifyClassApply.setId(null);
        modifyClassApply.setHandleTime(null);
        modifyClassApply.setStatus(null);
        modifyClassApply.setUserId(userId);
        modifyClassApply.setFromClassId(user.getClassId());
        modifyClassApply.setCreateTime(LocalDateTime.now());
        modifyClassApply.setToUserId(toClass.getUserId());
        boolean save = modifyClassApplyService.save(modifyClassApply);
        return save ? Result.success() : Result.error();
    }
}
