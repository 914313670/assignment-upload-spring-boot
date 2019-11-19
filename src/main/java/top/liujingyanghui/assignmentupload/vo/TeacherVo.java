package top.liujingyanghui.assignmentupload.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 老师Vo
 */
@Data
public class TeacherVo {

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
     * 工号
     */
    private String number;

    /**
     * 学校
     */
    private String school;

    /**
     * 注册时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;
}
