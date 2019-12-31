package top.liujingyanghui.assignmentupload.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.liujingyanghui.assignmentupload.dao.ClassMapper;
import top.liujingyanghui.assignmentupload.dao.SchoolMapper;
import top.liujingyanghui.assignmentupload.dao.UserMapper;
import top.liujingyanghui.assignmentupload.entity.Class;
import top.liujingyanghui.assignmentupload.entity.JwtUser;
import top.liujingyanghui.assignmentupload.entity.School;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.LoginVo;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserDetailsServiceImpl extends ServiceImpl<UserMapper, User> implements UserDetailsService, UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SchoolMapper schoolMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public JwtUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
        if (user == null) {
            log.info(email + "该用户不存在");
            throw new UsernameNotFoundException("用户名不存在");
        }
        return new JwtUser(user, Collections.singleton(new SimpleGrantedAuthority(user.getRole())));
    }

    @Override
    public LoginVo login(User user) {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        final Authentication authentication = authenticationManager.authenticate(upToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return createLoginVo(user.getEmail());
    }

    /**
     * 生成返回登录VO
     *
     * @param email
     * @return
     */
    private LoginVo createLoginVo(String email) {
        final JwtUser jwtUser = loadUserByUsername(email);
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("email", jwtUser.getUsername());
        tokenMap.put("role", jwtUser.getRole());
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(JwtUtil.setClaim(tokenMap, Long.toString(jwtUser.getId())));
        BeanUtils.copyProperties(jwtUser, loginVo);
        loginVo.setEmail(jwtUser.getUsername());
        School school = schoolMapper.selectById(jwtUser.getSchoolId());
        Class clazz = classMapper.selectById(jwtUser.getClassId());
        if (school != null) {
            loginVo.setSchoolName(school.getName());
        }
        if (clazz != null) {
            loginVo.setClassName(clazz.getName());
        }
        userMapper.update(null, Wrappers.<User>lambdaUpdate().eq(User::getId, jwtUser.getId()).set(User::getLastLoginTime, LocalDateTime.now()));
        return loginVo;
    }

    @Override
    public LoginVo getUserInfo(long id) {
        User user1 = userMapper.selectById(id);
        LoginVo loginVo = new LoginVo();
        BeanUtils.copyProperties(user1, loginVo);
        if (user1.getClassId() != null) {
            Class clazz = classMapper.selectById(user1.getClassId());
            loginVo.setClassName(clazz.getName());
        }
        if (user1.getSchoolId()!=null){
            School school = schoolMapper.selectById(user1.getSchoolId());
            loginVo.setSchoolName(school.getName());
        }
        return loginVo;
    }
}
