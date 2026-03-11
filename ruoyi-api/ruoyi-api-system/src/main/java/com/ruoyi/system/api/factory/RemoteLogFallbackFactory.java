package com.ruoyi.system.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.common.entity.R;
import com.ruoyi.system.api.RemoteLogService;

/**
 * 日志服务降级处理
 * 当日志微服务不可用时，提供降级返回，避免影响主流程
 * 操作日志记录各类用户（顶级管理员/店铺管理员/教练/会员）的操作行为
 * 登录日志记录所有用户的登录登出信息
 *
 * @author ruoyi
 */
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteLogFallbackFactory.class);

    @Override
    public RemoteLogService create(Throwable throwable) {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLogService() {
            @Override
            public R<Boolean> saveLog(OperLog operLog, String source) {
                return R.fail("保存操作日志失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> saveLogininfor(LoginInfo loginInfo, String source) {
                return R.fail("保存登录日志失败:" + throwable.getMessage());
            }
        };

    }
}
