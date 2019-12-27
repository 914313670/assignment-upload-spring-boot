package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.config.UrlConfig;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.EmailService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.vo.Result;
import top.liujingyanghui.assignmentupload.vo.ResultEnum;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UrlConfig urlConfig;

    @PostMapping("login")
    public Result login(@RequestBody User user) {
        return Result.success(userService.login(user));
    }

    @PostMapping("register")
    public Result register(@RequestBody User user) {
        user.setLastLoginTime(LocalDateTime.now());
        user.setCreateTime(LocalDateTime.now());
        User one = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, user.getEmail()));
        if (one != null) {
            return Result.error("该邮箱已注册！");
        }
        User one1 = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getNumber, user.getNumber()).eq(User::getSchoolId, user.getSchoolId()));
        if (one1 != null) {
            return user.getRole().equals("1") ? Result.error("该学校下此工号已经注册！") : Result.error("该学校下此学号已经注册！");
        }
        user.setRole(user.getRole().equals("1") ? "ROLE_TEACHER" : "ROLE_STUDENT");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setStatus(2);
        String code = UUID.randomUUID().toString();
        user.setActiveCode(code);
        boolean save = userService.save(user);
        String emailCodeUrl = urlConfig.getWebBaseUrl() + "active/" + user.getId() + "/" + code;
        String emailContent = "恭喜你注册成功，请点击下面链接进行激活！<br/><a href='" + emailCodeUrl + "'>" + emailCodeUrl +
                "</a><br/>如果点击没有反应请自行复制链接在浏览器打开！<br/>高校作业上传系统：<a href='" + urlConfig.getWebBaseUrl() + "'>" + urlConfig.getWebBaseUrl() +
                "</a>";
        emailService.sendHtmlEmail(user.getEmail(), "用户激活 - 高校作业上传系统", emailContent);
        return save ? Result.success() : Result.error();
    }

    @GetMapping("active")
    public Result active(@RequestParam long id, @RequestParam String activeCode) {
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getId, id).eq(User::getActiveCode, activeCode));
        if (user == null) {
            return Result.error(ResultEnum.ACTIVE_USER_FAIL);
        }
        if (user.getStatus() == 1) {
            return Result.error(ResultEnum.NOT_REPEAT_ACTIVE);
        }
        boolean update = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, id).set(User::getStatus, 1));
        return update ? Result.success("激活成功") : Result.error(ResultEnum.ACTIVE_USER_FAIL);
    }


}
