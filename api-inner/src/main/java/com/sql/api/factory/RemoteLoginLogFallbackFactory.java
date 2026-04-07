package com.sql.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sql.api.RemoteLoginLogService;
import com.sql.common.entity.po.LoginLog;
import com.sql.common.entity.result.R;

/**
 * 日志服务降级处理
 * 当日志微服务不可用时，提供降级返回，避免影响主流程
 * 登录日志记录所有用户的登录登出信息
 */
@Component
public class RemoteLoginLogFallbackFactory implements FallbackFactory<RemoteLoginLogService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteLoginLogFallbackFactory.class);

    @Override
    public RemoteLoginLogService create(Throwable throwable) {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLoginLogService() {
            @Override
            public R<Boolean> saveLoginLog(LoginLog loginLog, String source) {
                return R.fail("保存登录日志失败:" + throwable.getMessage());
            }
        };
    }
}
