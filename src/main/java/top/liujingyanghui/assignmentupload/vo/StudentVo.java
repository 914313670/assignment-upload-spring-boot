package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生Vo
 */
@Data
public class StudentVo {

    private Long id;

    /**
     *  邮箱
     */
    private String email;

    /**
     *  姓名
     */
    private String name;

    /**
     * 学号
     */
    private String number;

    /**
     * 班级名称
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
}
