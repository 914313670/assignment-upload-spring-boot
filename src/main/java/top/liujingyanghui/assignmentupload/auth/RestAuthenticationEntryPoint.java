package top.liujingyanghui.assignmentupload.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import top.liujingyanghui.assignmentupload.utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现AuthenticationEntryPoint的commence方法自定义校验不通过的方法
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 捕获AuthenticationException中的message，并封装成自定义异常抛出
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Map<String, Object> map = new HashMap<>();
        if (authException instanceof BadCredentialsException) { // 身份认证未通过
            map.put("status", 401);
            map.put("message", "用户名或密码错误，请重新输入！");
        } else if (authException instanceof DisabledException) {
            map.put("status", 401);
            map.put("message", "账号未激活！");
        } else if (authException instanceof LockedException) {
            map.put("status", 401);
            map.put("message", "账号已被锁定！");
        } else if (authException instanceof InsufficientAuthenticationException) {
            map.put("status", 401);
            map.put("message", "请先登陆在访问！");
        } else {
            map.put("status", 500);
            map.put("message", "未知错误！");
        }
        response.getWriter().write(JsonUtil.toJSON(map));
    }
}