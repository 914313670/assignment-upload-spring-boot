package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BusyworkVo {

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 要求
     */
    private String demand;

    /**
     * 课程Id
     */
    private Long courseId;

    /**
     * 已交数量
     */
    private Integer submitNum;

    /**
     * 未交数量
     */
    private Integer unpaidNum;

    /**
     * 附件地址
     */
    private String attachment;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 班级ID
     */
    private long classId;
}
