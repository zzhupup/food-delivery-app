package com.takeout.interceptor;

import com.takeout.common.BaseContext;
import com.takeout.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 检查请求是否携带有效的 Token
 *
 * @author 小好
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 预处理：在 Controller 方法执行前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 0. OPTIONS 预检请求直接放行（CORS）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 0.1 排除不需要认证的接口（双重保护）
        String requestURI = request.getRequestURI();
        log.info("【拦截器】requestURI={}, method={}", requestURI, request.getMethod());
        
        // 放行公开接口
        boolean isPublic = requestURI.startsWith("/user/") ||  // 所有用户接口（登录、注册、验证码）
                           requestURI.startsWith("/shop/") ||  // 店铺
                           requestURI.startsWith("/dish/") ||  // 菜品
                           requestURI.startsWith("/category/") ||  // 分类
                           requestURI.startsWith("/webjars/") ||  // 静态资源
                           requestURI.equals("/doc.html") ||
                           requestURI.startsWith("/v3/api-docs/") ||
                           requestURI.startsWith("/swagger-ui/");
        
        if (isPublic) {
            log.info("【拦截器】放行公开接口：{}", requestURI);
            return true;
        }

        // 1. 获取 Token（从 Header）
        String token = request.getHeader("Authorization");

        // 2. 检查 Token 是否存在
        if (token == null || token.isEmpty()) {
            log.warn("请求缺少 Authorization Header");
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录，请先登录\",\"data\":null}");
            return false;
        }

        // 3. 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 4. 验证 Token
        try {
            // 临时支持 mock token（开发环境）
            Long userId;
            if ("mock-token-123".equals(token)) {
                userId = 1L; // 测试用户 ID
                log.debug("使用 Mock Token，userId={}", userId);
            } else {
                userId = JwtUtil.verifyToken(token);
                log.debug("用户登录验证成功，userId={}", userId);
            }

            // 5. 将用户 ID 存入 ThreadLocal
            BaseContext.setCurrentId(userId);

            return true;

        } catch (Exception e) {
            log.warn("Token 验证失败：{}", e.getMessage());
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期\",\"data\":null}");
            return false;
        }
    }

    /**
     * 后处理：在 Controller 方法执行后调用
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理 ThreadLocal，防止内存泄漏
        BaseContext.removeCurrentId();
    }
}
