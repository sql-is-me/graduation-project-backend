package com.sql.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sql.common.constants.ServiceNameConstants;
import com.sql.api.factory.RemoteLoginLogFallbackFactory;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.po.LoginLog;
import com.sql.common.entity.result.R;

/**
 * 登录日志服务调用接口
 */
@FeignClient(contextId = "remoteLoginLogService", value = ServiceNameConstants.ADMIN_SERVICE, fallbackFactory = RemoteLoginLogFallbackFactory.class)
public interface RemoteLoginLogService {
        /**
         * 保存登录记录
         *
         * @param LoginLog 登录实体
         * @param source   请求来源
         * @return 结果
         */
        @PostMapping("/loginLog")
        public R<Boolean> saveLoginLog(@RequestBody LoginLog loginLog,
                        @RequestHeader(AuthConstants.FROM_SOURCE) String source);
}
