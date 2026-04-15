package com.sql.transaction.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.InnerAuth;

import lombok.Data;

/**
 * 模拟微信支付 JSAPI 接口
 * <p>
 * 模拟微信官方的统一下单和订单查询接口，用于开发/演示环境。
 * 参考：https://pay.weixin.qq.com/doc/v3/merchant/4012791861
 * </p>
 * <p>
 * 端点说明：
 * <ul>
 *   <li>{@code /unified-order}：商户后端调用，对应真实微信 JSAPI 下单，{@code @InnerAuth} 保护，仅允许 Feign 调用</li>
 *   <li>{@code /query-order/{outTradeNo}}：商户后端查单，{@code @InnerAuth} 保护，仅允许 Feign 调用</li>
 *   <li>{@code /do-pay}：模拟用户在微信客户端里"确认支付"，前端直接调用，需加入网关白名单放行</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/transaction/mock/wechat/pay")
public class MockWechatPayController {

    private static final String APPID = "wx_mock_appid";
    private static final String MCHID = "1600000001";

    /** 模拟的预支付订单存储：prepay_id -> 记录 */
    private final ConcurrentHashMap<String, PrepayRecord> prepayStore = new ConcurrentHashMap<>();

    /**
     * 模拟微信统一下单接口（JSAPI）
     * <p>
     * 仅允许商户后端通过 Feign 内部调用（携带 from-source=inner 请求头）。
     * </p>
     * 请求参数（简化）：
     * - out_trade_no: 商户订单号
     * - description: 商品描述
     * - total: 订单金额（分）
     */
    @InnerAuth
    @PostMapping("/unified-order")
    public Map<String, Object> unifiedOrder(@RequestBody Map<String, Object> request) {
        String outTradeNo = (String) request.get("out_trade_no");
        String description = (String) request.get("description");
        Object totalObj = request.get("total");

        Map<String, Object> result = new HashMap<>();

        if (outTradeNo == null || totalObj == null) {
            result.put("code", "PARAM_ERROR");
            result.put("message", "缺少必要参数 out_trade_no 或 total");
            return result;
        }

        int total;
        if (totalObj instanceof Number) {
            total = ((Number) totalObj).intValue();
        } else {
            String totalStr = totalObj.toString();
            if (totalStr.isEmpty()) {
                result.put("code", "PARAM_ERROR");
                result.put("message", "total 参数无效");
                return result;
            }
            total = Integer.parseInt(totalStr);
        }

        // 同一 out_trade_no 重复下单幂等：已存在未支付记录则直接返回
        PrepayRecord exist = prepayStore.values().stream()
                .filter(r -> outTradeNo.equals(r.outTradeNo) && "NOTPAY".equals(r.tradeState))
                .findFirst()
                .orElse(null);
        PrepayRecord record;
        if (exist != null) {
            record = exist;
        } else {
            record = new PrepayRecord();
            record.outTradeNo = outTradeNo;
            record.description = description;
            record.total = total;
            record.prepayId = "wx_prepay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            record.tradeState = "NOTPAY";
            record.createTime = LocalDateTime.now();
            prepayStore.put(record.prepayId, record);
        }

        // 构建前端调起支付所需参数（模拟 wx.requestPayment 参数）
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replace("-", "");

        result.put("code", "SUCCESS");
        result.put("prepay_id", record.prepayId);
        Map<String, String> payParams = new HashMap<>();
        payParams.put("appId", APPID);
        payParams.put("timeStamp", timeStamp);
        payParams.put("nonceStr", nonceStr);
        payParams.put("package", "prepay_id=" + record.prepayId);
        payParams.put("signType", "RSA");
        payParams.put("paySign", "mock_sign_" + nonceStr.substring(0, 8));
        result.put("pay_params", payParams);

        return result;
    }

    /**
     * 模拟用户完成支付
     * <p>
     * 前端调用此接口表示用户在模拟环境下"确认支付"（等价于真实微信中用户在微信客户端内点击确认）。
     * 将对应预支付订单的 trade_state 置为 SUCCESS。该接口不走内部调用限制，需在网关白名单放行。
     * </p>
     * 请求参数：prepay_id
     */
    @PostMapping("/do-pay")
    public Map<String, Object> doPay(@RequestBody Map<String, String> request) {
        String prepayId = request.get("prepay_id");
        Map<String, Object> result = new HashMap<>();

        if (prepayId == null || prepayId.isEmpty()) {
            result.put("code", "PARAM_ERROR");
            result.put("message", "缺少 prepay_id");
            return result;
        }

        PrepayRecord record = prepayStore.get(prepayId);
        if (record == null) {
            result.put("code", "ORDER_NOT_EXIST");
            result.put("message", "预支付订单不存在或已过期");
            return result;
        }

        if ("SUCCESS".equals(record.tradeState)) {
            result.put("code", "SUCCESS");
            result.put("message", "订单已支付");
            return result;
        }

        record.tradeState = "SUCCESS";
        record.transactionId = "MOCK_" + System.currentTimeMillis()
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        record.payTime = LocalDateTime.now();

        result.put("code", "SUCCESS");
        result.put("message", "支付成功");
        return result;
    }

    /**
     * 模拟微信订单查询接口（商户后端调用）
     * <p>
     * 对应微信官方：GET .../transactions/out-trade-no/{out_trade_no}
     * 仅允许商户后端通过 Feign 内部调用。
     * </p>
     */
    @InnerAuth
    @GetMapping("/query-order/{outTradeNo}")
    public Map<String, Object> queryOrder(@PathVariable String outTradeNo) {
        Map<String, Object> result = new HashMap<>();

        PrepayRecord record = prepayStore.values().stream()
                .filter(r -> outTradeNo.equals(r.outTradeNo))
                .findFirst()
                .orElse(null);

        if (record == null) {
            result.put("code", "ORDER_NOT_EXIST");
            result.put("message", "订单不存在");
            return result;
        }

        result.put("code", "SUCCESS");
        result.put("appid", APPID);
        result.put("mchid", MCHID);
        result.put("out_trade_no", record.outTradeNo);
        result.put("transaction_id", record.transactionId);
        result.put("trade_state", record.tradeState);
        result.put("trade_state_desc", "SUCCESS".equals(record.tradeState) ? "支付成功" : "未支付");
        Map<String, Object> amount = new HashMap<>();
        amount.put("total", record.total);
        amount.put("currency", "CNY");
        result.put("amount", amount);
        if (record.payTime != null) {
            result.put("success_time", record.payTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        return result;
    }

    /** 内部预支付记录 */
    @Data
    private static class PrepayRecord {
        String outTradeNo;
        String description;
        int total;
        String prepayId;
        String tradeState;
        String transactionId;
        LocalDateTime createTime;
        LocalDateTime payTime;
    }
}
