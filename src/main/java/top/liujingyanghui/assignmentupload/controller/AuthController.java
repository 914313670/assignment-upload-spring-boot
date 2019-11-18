package top.liujingyanghui.assignmentupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.vo.Result;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("api")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("auth/login")
    public Result login(@RequestBody User user){
        return Result.success(userService.login(user));
    }
}
