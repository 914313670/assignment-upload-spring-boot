package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

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
     * token
     */
    private String token;
}
