package top.liujingyanghui.assignmentupload.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
public class User {

    private Long id;

    /**
     * 密码
     */
    private String password;

    /**
     *  邮箱
     */
    private String email;

    /**
     *  角色
     */
    private String role;

    /**
     *  姓名
     */
    private String name;

    /**
     * 学号/工号
     */
    private String number;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 学校ID
     */
    private Integer schoolId;

    /**
     * 状态，1：正常使用，2：未激活
     */
    private Integer status;

    /**
     * 激活码
     */
    private String activeCode;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
