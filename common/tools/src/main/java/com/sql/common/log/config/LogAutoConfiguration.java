package com.sql.common.log.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sql.api.RemoteOperLogService;
import com.sql.common.log.aspect.LogAspect;
import com.sql.common.log.service.AsyncLogService;

/**
 * 日志自动配置，仅在存在 RemoteOperLogService（Feign Client）时才注册日志组件。
 * Gateway 等不使用 Feign 的模块不会加载。
 */
@Configuration
@ConditionalOnBean(RemoteOperLogService.class)
public class LogAutoConfiguration {

    @Bean
    public AsyncLogService asyncLogService() {
        return new AsyncLogService();
    }

    @Bean
    public LogAspect logAspect() {
        return new LogAspect();
    }
}
