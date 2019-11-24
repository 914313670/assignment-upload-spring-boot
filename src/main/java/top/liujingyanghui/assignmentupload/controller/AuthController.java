package top.liujingyanghui.assignmentupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.vo.LoginVo;
import top.liujingyanghui.assignmentupload.vo.Result;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody User user){
        return Result.success(userService.login(user));
    }

}
