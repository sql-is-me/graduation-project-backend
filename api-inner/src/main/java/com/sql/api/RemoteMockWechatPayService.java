package com.sql.api;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sql.api.factory.RemoteMockWechatPayFallbackFactory;
import com.sql.common.constants.ServiceNameConstants;

/**
 * 模拟微信支付服务调用接口（仅内部 Feign 调用）
 * <p>
 * 对应 transaction-service 中的 {@code MockWechatPayController} 中被
 * {@code @InnerAuth} 保护的端点。
 * 商户后端通过此接口调用"微信支付系统"完成统一下单和订单查询。
 * </p>
 */
@FeignClient(contextId = "remoteMockWechatPayService", value = ServiceNameConstants.TRANSACTION_SERVICE, fallbackFactory = RemoteMockWechatPayFallbackFactory.class)
public interface RemoteMockWechatPayService {

    /**
     * 统一下单（模拟微信 JSAPI 下单接口）
     *
     * @param request 包含 out_trade_no / description / total 的参数
     * @return 含 code / prepay_id / pay_params 的响应
     */
    @PostMapping("/transaction/mock/wechat/pay/unified-order")
    Map<String, Object> unifiedOrder(@RequestBody Map<String, Object> request);

    /**
     * 订单查询（模拟微信按商户订单号查单接口）
     *
     * @param outTradeNo 商户订单号
     * @return 含 code / trade_state / transaction_id / amount 的响应
     */
    @GetMapping("/transaction/mock/wechat/pay/query-order/{outTradeNo}")
    Map<String, Object> queryOrder(@PathVariable("outTradeNo") String outTradeNo);
}
