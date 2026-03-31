package com.sql.user.service;

import java.util.List;

import com.sql.common.entity.po.Order;

public interface OrderService {

    /**
     * 查询VIP自己的订单列表
     */
    List<Order> listMyOrders(String status);

    /**
     * 查询VIP自己的订单详情
     */
    Order getOrderDetail(Long orderId);
}