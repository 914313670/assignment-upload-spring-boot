package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生查看作业详情Vo
 * @author wdh
 * @date 2019/12/18 11:59
 */
@Data
public class BusyworkStudentDeatilsVo {

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
     * 提交状态，1：已提交但还未过期，2：已提交但已过期，3：未提交没过期，4：未提交已过期
     */
    private Integer status;

    /**
     * 作业url
     */
    private String url;

    /**
     * 课程名称
     */
    private String courseName;
}
