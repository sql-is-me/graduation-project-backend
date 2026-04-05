package com.sql.transaction.service;

import java.util.List;

import com.sql.common.entity.po.Order;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
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
     * 发起支付：调用模拟微信统一下单接口，返回前端调起支付所需参数
     */
    Object prepay(Long orderId);

    /**
     * 确认支付：调用模拟微信查询接口验证支付结果，完成订单
     */
    void confirmPay(Long orderId);
}