package com.ruoyi.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.ruoyi.api.RemoteOperLogService;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.common.entity.result.R;

/**
 * 日志服务降级处理
 * 当日志微服务不可用时，提供降级返回，避免影响主流程
 * 操作日志记录各类用户（顶级管理员/店铺管理员/教练/会员）的操作行为
 *
 * @author ruoyi
 */
@Component
public class RemoteOperLogFallbackFactory implements FallbackFactory<RemoteOperLogService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteOperLogFallbackFactory.class);

    @Override
    public RemoteOperLogService create(Throwable throwable) {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteOperLogService() {
            @Override
            public R<Boolean> saveOperLog(OperLog operLog, String source) {
                return R.fail("保存操作日志失败:" + throwable.getMessage());
            }
        };
    }
}
