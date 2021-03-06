package top.liujingyanghui.assignmentupload.entity;

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
     * 专业
     */
    @TableField(value = "specialty")
    private String specialty;

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
    private Long userId;

    /**
     * 创建人姓名
     */
    private String userName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}