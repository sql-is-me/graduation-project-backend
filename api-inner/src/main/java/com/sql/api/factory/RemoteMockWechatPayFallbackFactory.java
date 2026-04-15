package com.sql.api.factory;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.sql.api.RemoteMockWechatPayService;

/**
 * 模拟微信支付服务降级处理
 */
@Component
public class RemoteMockWechatPayFallbackFactory implements FallbackFactory<RemoteMockWechatPayService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteMockWechatPayFallbackFactory.class);

    @Override
    public RemoteMockWechatPayService create(Throwable throwable) {
        log.error("模拟微信支付服务调用失败:{}", throwable.getMessage());
        return new RemoteMockWechatPayService() {
            @Override
            public Map<String, Object> unifiedOrder(Map<String, Object> request) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", "SERVICE_UNAVAILABLE");
                result.put("message", "统一下单失败:" + throwable.getMessage());
                return result;
            }

            @Override
            public Map<String, Object> queryOrder(String outTradeNo) {
                Map<String, Object> result = new HashMap<>();
                result.put("code", "SERVICE_UNAVAILABLE");
                result.put("message", "订单查询失败:" + throwable.getMessage());
                return result;
            }
        };
    }
}
