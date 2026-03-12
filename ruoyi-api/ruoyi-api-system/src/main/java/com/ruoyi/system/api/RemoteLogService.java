package com.ruoyi.system.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ruoyi.common.Constants.ServiceNameConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.common.entity.R;
import com.ruoyi.system.api.factory.RemoteLogFallbackFactory;

/**
 * 日志服务调用接口
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService {
        /**
         * 保存系统日志
         *
         * @param OperLog 日志实体
         * @param source  请求来源
         * @return 结果
         */
        @PostMapping("/operLog")
        public R<Boolean> saveOperLog(@RequestBody OperLog operLog,
                        @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

        /**
         * 保存登录记录
         *
         * @param LoginInfo 登录实体
         * @param source    请求来源
         * @return 结果
         */
        @PostMapping("/loginInfo")
        public R<Boolean> saveLoginInfo(@RequestBody LoginInfo loginInfo,
                        @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
