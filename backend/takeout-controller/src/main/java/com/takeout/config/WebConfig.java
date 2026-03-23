package com.takeout.config;

import com.takeout.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 * 配置拦截器
 *
 * @author 小好
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 拦截所有 API 路径
                .addPathPatterns("/**")
                // 排除不需要认证的接口
                .excludePathPatterns(
                        "/user/codes",           // 发送验证码
                        "/user/tokens",          // 登录
                        "/user/registration",    // 注册
                        "/shop/**",              // 店铺（公开浏览）
                        "/dish/**",              // 菜品（公开浏览）
                        "/category/**",          // 分类（公开浏览）
                        // 静态资源和 API 文档
                        "/webjars/**",
                        "/doc.html",
                        "/v3/api-docs/**",
                        "/swagger-ui/**"
                );
    }
}
