package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wdh
 * @date 2019/12/13 17:37
 */
@Data
public class BusyworkStudentPageVo {

    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 已交数量
     */
    private Integer submitNum;

    /**
     * 未交数量
     */
    private Integer unpaidNum;

    /**
     * 提交状态，1：已提交但还未过期，2：已提交但已过期，3：未提交没过期，4：未提交已过期
     */
    private Integer status;

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
}
