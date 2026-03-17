package com.sql.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.sql.api.factory.RemoteClassHourFallbackFactory;
import com.sql.common.constants.AuthConstants;
import com.sql.common.constants.ServiceNameConstants;
import com.sql.common.entity.result.R;

/**
 * 课时服务内部调用接口
 */
@FeignClient(contextId = "remoteClassHourService", value = ServiceNameConstants.ADMIN_SERVICE, fallbackFactory = RemoteClassHourFallbackFactory.class)
public interface RemoteClassHourService {

    /**
     * 增加用户课时
     */
    @PostMapping("/classHour/innerAdd")
    R<Boolean> addClassHours(@RequestParam("userId") Long userId,
            @RequestParam("hours") int hours,
            @RequestHeader(AuthConstants.FROM_SOURCE) String source);
}
