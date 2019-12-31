package top.liujingyanghui.assignmentupload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 转班申请
 * @author  wdh
 * @date  2019/12/31 16:48
 */
@Data
@TableName(value = "modify_class_apply")
public class ModifyClassApply {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 转班前班级ID
     */
    @TableField(value = "from_class_id")
    private Long fromClassId;

    /**
     * 转班前班级名
     */
    private String fromClassName;


    /**
     * 转班后班级ID
     */
    @TableField(value = "to_class_id")
    private Long toClassId;

    /**
     * 转班后班级名
     */
    private String toClassName;

    /**
     * 申请用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 申请人姓名
     */
    private String userName;

    /**
     * 申请转班的班级创建者
     */
    @TableField(value = "to_user_id")
    private Long toUserId;

    /**
     * 状态，0：不同意，1：同意
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 申请时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 操作时间
     */
    @TableField(value = "handle_time")
    private LocalDateTime handleTime;

}