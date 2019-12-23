package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录Vo
 */
@Data
public class LoginVo {

    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 角色
     */
    private String role;

    /**
     * 学校ID
     */
    private Integer schoolId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 学号/工号
     */
    private String number;

    /**
     * 学校名
     */
    private String schoolName;

    /**
     * 班级名
     */
    private String className;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * token
     */
    private String token;
}
