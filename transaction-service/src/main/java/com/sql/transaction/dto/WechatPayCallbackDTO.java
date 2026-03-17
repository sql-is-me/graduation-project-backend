package com.sql.transaction.dto;

import lombok.Data;

/**
 * 微信支付回调参数
 * 实际接入微信支付时根据微信文档调整字段
 */
@Data
public class WechatPayCallbackDTO {
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 微信支付交易号
     */
    private String transactionId;

    /**
     * 支付结果 SUCCESS-成功
     */
    private String resultCode;
}
