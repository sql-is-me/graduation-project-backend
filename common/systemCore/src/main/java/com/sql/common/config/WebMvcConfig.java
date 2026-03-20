package com.sql.common.config;

import org.springframework.context.annotation.Bean;
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
    public static final String[] excludeUrls = { "/admin/auth/login", "/admin/auth/register", "/admin/auth/emailCode",
            "/admin/auth/resetPassword",
            "/user/auth/login", "/user/auth/register" }; // TODO: 其他不需要拦截的地址

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
    @Bean
    public HeaderInterceptor getHeaderInterceptor() {
        return new HeaderInterceptor();
    }
}
