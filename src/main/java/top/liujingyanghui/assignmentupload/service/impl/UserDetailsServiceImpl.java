package top.liujingyanghui.assignmentupload.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import top.liujingyanghui.assignmentupload.dao.UserMapper;
import top.liujingyanghui.assignmentupload.entity.JwtUser;
import top.liujingyanghui.assignmentupload.entity.User;
import top.liujingyanghui.assignmentupload.exception.MyException;
import top.liujingyanghui.assignmentupload.service.UserService;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.LoginVo;
import top.liujingyanghui.assignmentupload.vo.ResultEnum;

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
    private AuthenticationManager authenticationManager;

    @Override
    public JwtUser loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getEmail, email));
        if (user == null) {
            log.info(email + "该用户不存在");
            throw new UsernameNotFoundException("用户名不存在");
        }
        return new JwtUser(user.getId(), user.getEmail(), user.getPassword(), user.getRole(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),user.getName());
    }

    @Override
    public LoginVo register(User user) {
        Integer count = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getEmail, user.getEmail()));
        if (1 == count) {
            throw new MyException(ResultEnum.USER_IS_EXIST);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String priPassword = user.getPassword();
        user.setPassword(encoder.encode(priPassword));
        user.setRole("ROLE_ADMIN");
        user.setCreateTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.insert(user);
        return createLoginVo(user.getEmail());
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
        loginVo.setToken(JwtUtil.setClaim(tokenMap, email));
        loginVo.setName(jwtUser.getName());
        loginVo.setRole(jwtUser.getRole());
        return loginVo;
    }
}
