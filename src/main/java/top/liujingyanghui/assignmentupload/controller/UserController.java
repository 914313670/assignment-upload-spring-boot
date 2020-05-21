package top.liujingyanghui.assignmentupload.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.entity.BusyworkUpload;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.BusyworkUploadService;
import top.liujingyanghui.assignmentupload.service.ClassService;
import top.liujingyanghui.assignmentupload.service.SchoolService;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.LoginVo;
import top.liujingyanghui.assignmentupload.vo.PageVo;
import top.liujingyanghui.assignmentupload.vo.Result;
import top.liujingyanghui.assignmentupload.vo.StudentVo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassService classService;
    @Autowired
    private BusyworkUploadService busyworkUploadService;
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
    public Result teacherPage(@RequestParam int schoolId, @RequestParam String email, @RequestParam String number,
                              @RequestParam String name, @RequestParam(defaultValue = "1") int current,
                              @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = new Page<>(current, size);
        IPage<User> page = userService.page(userPage, Wrappers.<User>lambdaQuery().select(User::getId, User::getEmail,
                User::getName, User::getNumber, User::getCreateTime, User::getLastLoginTime)
                .eq(User::getRole, "ROLE_TEACHER").eq(User::getSchoolId, schoolId).like(User::getEmail, email).
                        like(User::getNumber, number).like(User::getName, name));
        return Result.success(page);
    }

    /**
     * 学生分页
     *
     * @param schoolId 学校id
     * @param email    邮箱
     * @param number   工号
     * @param name     姓名
     * @param current  当前页
     * @param size     每页大小
     * @return
     */
    @GetMapping("studentPage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public Result studentPage(@RequestParam int schoolId, @RequestParam int classId, @RequestParam String email, @RequestParam String number, @RequestParam String name,
                              @RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = new Page<>(current, size);
        IPage<User> page = userService.page(userPage, Wrappers.<User>lambdaQuery().select(User::getId, User::getEmail, User::getName, User::getNumber, User::getClassId,
                User::getCreateTime, User::getLastLoginTime).eq(User::getRole, "ROLE_STUDENT").eq(User::getSchoolId, schoolId).eq(-1 != classId,
                User::getClassId, classId).like(User::getEmail, email).like(User::getNumber, number).like(User::getName, name));
        PageVo<StudentVo> pageVo = new PageVo<>();
        pageVo.setTotal(page.getTotal());
        pageVo.setSize(page.getSize());
        pageVo.setCurrent(page.getCurrent());
        ArrayList<StudentVo> studentVos = new ArrayList<>();
        Map<Long, Class> map = new HashMap<>();
        for (User item : page.getRecords()) {
            StudentVo studentVo = new StudentVo();
            BeanUtils.copyProperties(item, studentVo);
            Class clazz;
            if (item.getClassId() != null) {
                if (map.containsKey(item.getClassId())) {
                    clazz = map.get(item.getClassId());
                } else {
                    clazz = classService.getById(item.getClassId());
                    map.put(item.getClassId(), clazz);
                }
                studentVo.setClassName(clazz.getName());
                studentVo.setClassCreateName(clazz.getUserName());
                studentVo.setClassCreateId(clazz.getUserId());
                studentVos.add(studentVo);
            } else {
                studentVo.setClassName("无");
                studentVo.setClassCreateName("无");
                studentVos.add(studentVo);
            }
        }
        pageVo.setRecords(studentVos);
        return Result.success(pageVo);
    }

    /**
     * 老师信息修改
     *
     * @param user
     * @return
     */
    @PutMapping("teacherUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    public Result update(@RequestBody User user) {
        User user1 = userService.getById(user.getId());
        User user2 = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getRole, user1.getRole()).eq(User::getSchoolId, user.getSchoolId()).eq(User::getNumber, user.getNumber()).ne(User::getId, user.getId()));
        if (user2 != null) {
            return Result.error(user1.getRole().equals("ROLE_STUDENT") ? "学号已存在" : "工号已存在");
        }
        boolean update = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, user.getId()).set(User::getName, user.getName()).set(User::getNumber, user.getNumber())
                .set(User::getSchoolId, user.getSchoolId()));
        return update ? Result.success() : Result.error("修改失败");
    }

    /**
     * 从班级移除此学生
     *
     * @param request
     * @param id
     * @return
     */
    @DeleteMapping("remove")
    @PreAuthorize("hasRole('TEACHER')")
    public Result remove(HttpServletRequest request, @RequestParam Long id) {
        // 判断老师是否拥有修改权限
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long teacherId = JwtUtil.getSubject(token);
        User user = userService.getById(id);
        Class clazz = classService.getById(user.getClassId());
        if (!clazz.getUserId().equals(teacherId)) {
            return Result.error("你不是班级创建人，没有权限");
        }
        boolean update = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, id).set(User::getClassId, null));
        busyworkUploadService.remove(Wrappers.<BusyworkUpload>lambdaQuery().eq(BusyworkUpload::getClassId, clazz.getId()).eq(BusyworkUpload::getUserId, id));
        return update ? Result.success() : Result.error("移出失败");
    }

    /**
     * 获取老师数量
     */
    @GetMapping("teacher-count")
    public Result teacherCount(@RequestParam int schoolId) {
        return Result.success(userService.count(Wrappers.<User>lambdaQuery().eq(User::getRole, "ROLE_TEACHER").eq(schoolId != -1, User::getSchoolId, schoolId)));
    }

    /**
     * 获取学生数量
     */
    @GetMapping("student-count")
    public Result studentCount(@RequestParam int schoolId) {
        return Result.success(userService.count(Wrappers.<User>lambdaQuery().eq(User::getRole, "ROLE_STUDENT").eq(schoolId != -1, User::getSchoolId, schoolId)));
    }

    /**
     * 更新用户信息（姓名，学号/工号）
     *
     * @param request
     * @param user
     * @return
     */
    @PutMapping("updateUserInfo")
    public Result updateUserInfo(HttpServletRequest request, @RequestBody User user) {
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long userId = JwtUtil.getSubject(token);
        boolean update = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, userId).set(User::getNumber, user.getNumber()).set(User::getName, user.getName()));
        LoginVo loginVo = userService.getUserInfo(userId);
        return update ? Result.success(loginVo) : Result.error("修改失败");
    }

    /**
     * 密码修改
     *
     * @param request
     * @param map
     * @return
     */
    @PutMapping("updatePassword")
    public Result updatePassword(HttpServletRequest request, @RequestBody Map<String, String> map) {
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long userId = JwtUtil.getSubject(token);
        User user = userService.getById(userId);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(map.get("priPassword"), user.getPassword())) {
            return Result.error("原密码错误！");
        }
        String password = map.get("password");
        if (password == null || password.trim().equals("")) {
            return Result.error("请输入新密码");
        }
        boolean update = userService.update(Wrappers.<User>lambdaUpdate().eq(User::getId, userId).set(User::getPassword, bCryptPasswordEncoder.encode(password)));
        return update ? Result.success() : Result.error();
    }
}
