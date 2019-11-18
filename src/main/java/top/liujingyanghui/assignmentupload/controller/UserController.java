package top.liujingyanghui.assignmentupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.liujingyanghui.assignmentupload.service.UserService;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("api")
public class UserController {
    @Autowired
    private UserService userService;


}
