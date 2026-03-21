package com.sql.transaction.service;

import java.util.List;

import com.sql.common.entity.db.Order;

public interface AdminOrderService {

    /**
     * 查询系统全部订单(admin用)
     */
    List<Order> listAllOrders(String status);

    /**
     * 查询当前店铺的全部订单(manager用)
     */
    List<Order> listStoreOrders(String status);
}
