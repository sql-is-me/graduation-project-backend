package com.sql.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sql.api.RemoteClassHourService;
import com.sql.common.entity.result.R;

/**
 * 课时服务降级处理
 */
@Component
public class RemoteClassHourFallbackFactory implements FallbackFactory<RemoteClassHourService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteClassHourFallbackFactory.class);

    @Override
    public RemoteClassHourService create(Throwable throwable) {
        log.error("课时服务调用失败:{}", throwable.getMessage());
        return new RemoteClassHourService() {
            @Override
            public R<Boolean> addClassHours(Long userId, int hours, String source) {
                return R.fail("增加课时失败:" + throwable.getMessage());
            }
        };
    }
}