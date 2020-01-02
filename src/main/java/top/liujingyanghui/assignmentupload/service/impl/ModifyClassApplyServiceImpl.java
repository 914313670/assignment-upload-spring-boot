package top.liujingyanghui.assignmentupload.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import top.liujingyanghui.assignmentupload.dao.ModifyClassApplyMapper;
import top.liujingyanghui.assignmentupload.dao.UserMapper;
import top.liujingyanghui.assignmentupload.entity.ModifyClassApply;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.exception.MyException;
import top.liujingyanghui.assignmentupload.service.ModifyClassApplyService;
import top.liujingyanghui.assignmentupload.vo.ResultEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wdh
 * @date 2019/12/31 16:48
 */
@Service
@Transactional
public class ModifyClassApplyServiceImpl extends ServiceImpl<ModifyClassApplyMapper, ModifyClassApply> implements ModifyClassApplyService {

    @Autowired
    private ModifyClassApplyMapper modifyClassApplyMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean agree(long userId, long id) {
        ModifyClassApply modifyClassApply = modifyClassApplyMapper.selectById(id);
        if (userId != modifyClassApply.getToUserId()) {
            throw new MyException(ResultEnum.NOT_AUTH);
        }
        int update = userMapper.update(null, Wrappers.<User>lambdaUpdate().eq(User::getId, modifyClassApply.getUserId()).set(User::getClassId, modifyClassApply.getToClassId()));
        if (update < 1) {
            return false;
        }
        int update1 = modifyClassApplyMapper.update(null, Wrappers.<ModifyClassApply>lambdaUpdate().eq(ModifyClassApply::getId, modifyClassApply.getId()).set(ModifyClassApply::getStatus, 1).
                set(ModifyClassApply::getHandleTime, LocalDateTime.now()));
        if (update1 < 1) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    @Override
    public boolean batchAgree(long userId, List<String> ids) {
        for (String id : ids) {
            ModifyClassApply modifyClassApply = modifyClassApplyMapper.selectById(id);
            if (userId != modifyClassApply.getToUserId()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new MyException(ResultEnum.NOT_AUTH);
            }
            int update = userMapper.update(null, Wrappers.<User>lambdaUpdate().eq(User::getId, modifyClassApply.getUserId()).set(User::getClassId, modifyClassApply.getToClassId()));
            if (update < 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            int update1 = modifyClassApplyMapper.update(null, Wrappers.<ModifyClassApply>lambdaUpdate().eq(ModifyClassApply::getId, modifyClassApply.getId()).set(ModifyClassApply::getStatus, 1).
                    set(ModifyClassApply::getHandleTime, LocalDateTime.now()));
            if (update1 < 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean batchOppose(long userId, List<String> ids) {
        for (String id : ids) {
            ModifyClassApply modifyClassApply = modifyClassApplyMapper.selectById(id);
            if (userId != modifyClassApply.getToUserId()) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new MyException(ResultEnum.NOT_AUTH);
            }
            int update = modifyClassApplyMapper.update(null, Wrappers.<ModifyClassApply>lambdaUpdate().eq(ModifyClassApply::getId, modifyClassApply.getId()).set(ModifyClassApply::getStatus, 0).
                    set(ModifyClassApply::getHandleTime, LocalDateTime.now()));
            if (update<1){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
        }
        return false;
    }
}
