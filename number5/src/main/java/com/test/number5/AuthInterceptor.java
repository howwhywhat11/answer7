package com.test.number5;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //前端把 Token 放在请求头（Header）的 "Authorization" 里面（根据需要修改）
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"您还没登录，请先登录获取 Token\"}");
            return false;        }

        try {
            Claims claims = jwtUtil.parseToken(token);

            // 获取密钥代表的用户名和身份
            String username = claims.getSubject();
            String role = (String) claims.get("role");

            // 把信息设置到 Request 里，让 Controller 直接使用
            request.setAttribute("currentUser", username);
            request.setAttribute("currentRole", role);

            return true;

        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\": \"Token 无效或已过期，请重新登录\"}");
            return false;
        }
    }
}
