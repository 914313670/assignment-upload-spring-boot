package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程Vo
 */
@Data
public class CourseVo {

    private Long id;

    /**
     * 课程名
     */
    private String name;

    /**
     * 作业数量
     */
    private Integer busyworkNum;

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 班级名
     */
    private String className;

    /**
     * 老师姓名
     */
    private String teacherName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
