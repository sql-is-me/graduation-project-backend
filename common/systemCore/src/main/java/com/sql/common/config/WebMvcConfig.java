package com.sql.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sql.common.interceptor.HeaderInterceptor;

/**
 * 拦截器配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /** 不需要拦截地址 */
    public static final String[] excludeUrls = { "/admin/auth/login", "/admin/auth/logout", "/admin/auth/refresh",
            "/user/auth/login", "/user/auth/logout", "/user/auth/refresh" }; // FIXME:验证是否可靠

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getHeaderInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls)
                .order(-10);
    }

    /**
     * 自定义请求头拦截器
     */
    public HeaderInterceptor getHeaderInterceptor() {
        return new HeaderInterceptor();
    }
}
