package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作业提交记录Vo
 * @author wdh
 * @date 2019/12/18 17:55
 */
@Data
public class BusyworkRecordVo {

    /**
     * 学号
     */
    private String number;

    /**
     * 姓名
     */
    private String name;

    /**
     * 提交状态，1：已提交，2：未提交
     */
    private Integer status;

    /**
     * 作业地址
     */
    private String url;

    /**
     * 提交时间
     */
    private LocalDateTime createTime;
}
