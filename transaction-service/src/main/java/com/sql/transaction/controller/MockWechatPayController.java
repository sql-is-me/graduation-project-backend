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

/**
 * 模拟微信支付 JSAPI 接口
 * <p>
 * 模拟微信官方的统一下单和订单查询接口，用于开发/演示环境。
 * 参考：https://pay.weixin.qq.com/doc/v3/merchant/4012791861
 * </p>
 */
@RestController
@RequestMapping("/mock/wechat/pay")
public class MockWechatPayController {

    private static final String APPID = "wx_mock_appid";
    private static final String MCHID = "1600000001";

    /** 模拟的预支付订单存储：prepay_id -> out_trade_no */
    private final ConcurrentHashMap<String, PrepayRecord> prepayStore = new ConcurrentHashMap<>();

    /**
     * 模拟微信统一下单接口（JSAPI）
     * <p>
     * 请求参数（简化）：
     * - out_trade_no: 商户订单号
     * - description: 商品描述
     * - total: 订单金额（分）
     * </p>
     * 返回 prepay_id 及前端调起支付所需参数
     */
    @PostMapping("/unified-order")
    public Map<String, Object> unifiedOrder(@RequestBody Map<String, Object> request) {
        String outTradeNo = (String) request.get("out_trade_no");
        // String description = (String) request.get("description");
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
            total = Integer.parseInt(totalObj.toString());
        }

        // 生成 prepay_id
        String prepayId = "wx_prepay_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        // 存储预支付记录
        PrepayRecord record = new PrepayRecord();
        record.outTradeNo = outTradeNo;
        // record.description = description;
        record.total = total;
        // record.prepayId = prepayId;
        record.tradeState = "NOTPAY";
        // record.createTime = LocalDateTime.now();
        prepayStore.put(prepayId, record);

        // 构建前端调起支付所需参数（模拟 wx.requestPayment 参数）
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = UUID.randomUUID().toString().replace("-", "");

        result.put("code", "SUCCESS");
        result.put("prepay_id", prepayId);
        // 以下为前端 wx.requestPayment 所需参数
        Map<String, String> payParams = new HashMap<>();
        payParams.put("appId", APPID);
        payParams.put("timeStamp", timeStamp);
        payParams.put("nonceStr", nonceStr);
        payParams.put("package", "prepay_id=" + prepayId);
        payParams.put("signType", "RSA");
        payParams.put("paySign", "mock_sign_" + nonceStr.substring(0, 8));
        result.put("pay_params", payParams);

        return result;
    }

    /**
     * 模拟用户完成支付
     * <p>
     * 前端调用此接口表示用户在模拟环境下"确认支付"，
     * 将对应预支付订单的 trade_state 置为 SUCCESS。
     * </p>
     * 请求参数：prepay_id
     */
    @PostMapping("/do-pay")
    public Map<String, Object> doPay(@RequestBody Map<String, String> request) {
        String prepayId = request.get("prepay_id");
        Map<String, Object> result = new HashMap<>();

        if (prepayId == null) {
            result.put("code", "PARAM_ERROR");
            result.put("message", "缺少 prepay_id");
            return result;
        }

        PrepayRecord record = prepayStore.get(prepayId);
        if (record == null) {
            result.put("code", "ORDER_NOT_EXIST");
            result.put("message", "预支付订单不存在");
            return result;
        }

        // 模拟支付成功
        record.tradeState = "SUCCESS";
        record.transactionId = "MOCK_" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        record.payTime = LocalDateTime.now();

        result.put("code", "SUCCESS");
        result.put("message", "支付成功");
        return result;
    }

    /**
     * 模拟微信订单查询接口（商户后端调用）
     * <p>
     * 对应微信官方：GET .../transactions/out-trade-no/{out_trade_no}
     * 由后端在确认支付时调用，验证支付结果。
     * </p>
     */
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
    private static class PrepayRecord {
        String outTradeNo;
        // String description;
        int total;
        // String prepayId;
        String tradeState;
        String transactionId;
        // LocalDateTime createTime;
        LocalDateTime payTime;
    }
}
