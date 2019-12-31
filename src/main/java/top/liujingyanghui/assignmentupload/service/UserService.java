package top.liujingyanghui.assignmentupload.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.vo.LoginVo;

public interface UserService extends IService<User> {

    /**
     * 登录
     * @param user
     * @return
     */
    LoginVo login(User user);

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    LoginVo getUserInfo(long id);
}
