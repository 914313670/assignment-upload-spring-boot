package top.liujingyanghui.assignmentupload.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "class")
public class Class {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年级
     */
    @TableField(value = "grade")
    private Integer grade;

    /**
     * 班级名
     */
    @TableField(value = "name")
    private String name;

    /**
     * 班级人数
     */
    private Integer number;

    /**
     * 学校ID
     */
    @TableField(value = "school_id")
    private Integer schoolId;

    /**
     * 创建人ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}