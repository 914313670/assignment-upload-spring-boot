package top.liujingyanghui.assignmentupload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
     * 学校ID
     */
    @TableField(value = "school_id")
    private Integer schoolId;

    public static final String COL_ID = "id";

    public static final String COL_GRADE = "grade";

    public static final String COL_NAME = "name";

    public static final String COL_SCHOOL_ID = "school_id";
}