package top.liujingyanghui.assignmentupload.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
public class Course {

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
     * 班级ID
     */
    private Long classId;

    /**
     * 学校ID
     */
    private Integer schoolId;

    /**
     * 老师ID
     */
    private Long teacherId;

    /**
     * 老师姓名
     */
    private String teacherName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
