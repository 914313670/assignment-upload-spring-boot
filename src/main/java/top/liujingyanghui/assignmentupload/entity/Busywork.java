package top.liujingyanghui.assignmentupload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "busywork")
public class Busywork {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 要求
     */
    @TableField(value = "demand")
    private String demand;

    /**
     * 课程Id
     */
    @TableField(value = "course_id")
    private Long courseId;

    /**
     * 已交数量
     */
    @TableField(exist = false)
    private Integer submitNum;

    /**
     * 未交数量
     */
    @TableField(exist = false)
    private Integer unpaidNum;

    /**
     * 附件地址
     */
    @TableField(value = "attachment")
    private String attachment;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    public static final String COL_ID = "id";

    public static final String COL_TITLE = "title";

    public static final String COL_DEMAND = "demand";

    public static final String COL_COURSE_ID = "course_id";

    public static final String COL_SUBMIT_NUM = "submit_num";

    public static final String COL_UNPAID_NUM = "unpaid_num";

    public static final String COL_ATTACHMENT = "attachment";

    public static final String COL_END_TIME = "end_time";

    public static final String COL_CREATE_TIME = "create_time";
}