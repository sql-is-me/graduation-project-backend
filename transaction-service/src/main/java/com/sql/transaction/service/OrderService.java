package com.sql.transaction.service;

import com.sql.common.entity.db.Order;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
import com.sql.transaction.dto.WechatPayCallbackDTO;

public interface OrderService {

    /**
     * VIP创建课时购买订单
     */
    Order createOrder(OrderCreateDTO dto);

    /**
     * VIP取消订单(仅待支付状态可取消)
     */
    int cancelOrder(Long orderId, OrderCancelDTO dto);

    /**
     * 发起微信支付(返回预支付参数)
     */
    Object prepayWechat(Long orderId);

    /**
     * 微信支付回调处理
     */
    String wechatPayCallback(WechatPayCallbackDTO dto);
}