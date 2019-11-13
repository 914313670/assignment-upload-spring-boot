package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.util.List;

/**
 * 学校级联选择器VO
 */
@Data
public class SchoolCascaderVo {

    private int id;

    private String name;

    private List<SchoolCascaderVo> children;
}
