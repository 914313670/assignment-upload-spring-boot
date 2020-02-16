package top.liujingyanghui.assignmentupload.entity;

import lombok.Data;

/**
 * 学校实体
 */
@Data
public class School {

    private Integer id;

    /**
     * 学校名
     */
    private String name;

    /**
     * 是否删除（0：否，1：是）
     */
    private Integer delFlag;

    /**
     * 城市Id
     */
    private Integer cityId;
}
