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
     * 提交状态，1：已提交，2：未提交，3：已过期
     */
    private Integer status;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
