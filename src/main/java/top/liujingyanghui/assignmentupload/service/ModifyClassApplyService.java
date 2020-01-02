package top.liujingyanghui.assignmentupload.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.liujingyanghui.assignmentupload.entity.ModifyClassApply;

import java.util.List;

/**
 * @author wdh
 * @date 2019/12/31 16:48
 */
public interface ModifyClassApplyService extends IService<ModifyClassApply> {

    /**
     * 审核同意
     *
     * @param userId 用户id
     * @param id     申请id
     * @return
     */
    boolean agree(long userId, long id);

    /**
     * 审核批量同意
     *
     * @param userId
     * @param ids
     * @return
     */
    boolean batchAgree(long userId, List<String> ids);

    /**
     * 审核批量拒绝
     *
     * @param userId
     * @param ids
     * @return
     */
    boolean batchOppose(long userId, List<String> ids);
}
