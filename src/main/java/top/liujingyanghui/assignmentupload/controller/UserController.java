package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.SchoolService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.vo.Result;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SchoolService schoolService;

    /**
     * 老师分页
     *
     * @param schoolId 学校id
     * @param email    邮箱
     * @param number   工号
     * @param name     姓名
     * @param current  当前页
     * @param size     每页大小
     * @return
     */
    @GetMapping("teacherPage")
    @PreAuthorize("hasRole('ADMIN')")
    public Result page(@RequestParam int schoolId, @RequestParam String email, @RequestParam String number, @RequestParam String name, @RequestParam(defaultValue = "1") int current,
                       @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = new Page<>(current, size);
        IPage<User> page;
        if (-1 == schoolId) {
            page = userService.page(userPage, Wrappers.<User>lambdaQuery().select(User::getId, User::getEmail, User::getName, User::getNumber, User::getCreateTime, User::getLastLoginTime)
                    .eq(User::getRole, "ROLE_TEACHER").like(User::getEmail, email).like(User::getNumber, number).like(User::getName, name));
        } else {
            page = userService.page(userPage, Wrappers.<User>lambdaQuery().select(User::getId, User::getEmail, User::getName, User::getNumber, User::getCreateTime, User::getLastLoginTime)
                    .eq(User::getRole, "ROLE_TEACHER").eq(User::getSchoolId, schoolId).like(User::getEmail, email).like(User::getNumber, number).like(User::getName, name));
        }
        return Result.success(page);
    }
}
