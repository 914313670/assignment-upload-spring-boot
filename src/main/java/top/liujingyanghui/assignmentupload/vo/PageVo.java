package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页
 */
@Data
public class PageVo<T> {

    /**
     * 当前页
     */
    private long current;

    /**
     * 每页数量
     */
    private long size;

    /**
     * 总数据量
     */
    private long total;

    /**
     * 当前页数据
     */
    private List<T> records;
}
