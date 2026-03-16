package com.sql.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sql.common.constants.ServiceNameConstants;
import com.sql.api.factory.RemoteOperLogFallbackFactory;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.OperLog;
import com.sql.common.entity.result.R;

/**
 * 操作日志服务调用接口
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.ALL_SERVICE, fallbackFactory = RemoteOperLogFallbackFactory.class)
public interface RemoteOperLogService {
        /**
         * 保存系统日志
         *
         * @param OperLog 日志实体
         * @param source  请求来源
         * @return 结果
         */
        @PostMapping("/operLog")
        public R<Boolean> saveOperLog(@RequestBody OperLog operLog,
                        @RequestHeader(AuthConstants.FROM_SOURCE) String source);
}
