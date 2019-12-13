package top.liujingyanghui.assignmentupload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author  wdh
 * @date  2019/12/13 17:52
 */
@Data
@TableName(value = "busywork_upload")
public class BusyworkUpload {
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 学生ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 作业ID
     */
    @TableField(value = "busywork_id")
    private Long busyworkId;

    /**
     * 附件地址
     */
    @TableField(value = "url")
    private String url;

    /**
     * 提交时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_BUSYWORK_ID = "busywork_id";

    public static final String COL_URL = "url";

    public static final String COL_CREATE_TIME = "create_time";
}