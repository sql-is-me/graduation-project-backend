package com.sql.transaction.service;

import java.util.List;

import com.sql.common.entity.po.Order;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
import com.sql.transaction.dto.WechatPayCallbackDTO;

public interface OrderService {

    /**
     * VIP创建订单（课时或套餐）
     */
    Order createOrder(OrderCreateDTO dto);

    /**
     * VIP取消订单（仅待支付状态可取消）
     */
    int cancelOrder(Long orderId, OrderCancelDTO dto);

    /**
     * 查询当前用户的订单列表
     */
    List<Order> listMyOrders(String status);

    /**
     * 查询单笔订单详情（仅本人）
     */
    Order getMyOrder(Long orderId);

    /**
     * 模拟支付（直接将订单置为已支付，用于演示）
     */
    void mockPay(Long orderId);

    /**
     * 发起微信支付（返回预支付参数）
     */
    Object prepayWechat(Long orderId);

    /**
     * 微信支付回调处理
     */
    String wechatPayCallback(WechatPayCallbackDTO dto);
}