package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.po.Order;

/**
 * 管理员订单查询服务
 */
public interface AdminOrderService {

    /**
     * 系统管理员查询全部订单
     * @param storeId 按店铺筛选（可选）
     * @param status  按状态筛选（可选）：0-待支付 1-已支付 2-已取消 3-已退款
     */
    List<Order> listAllOrders(Long storeId, String status);

    /**
     * 店铺管理员查询本店铺订单
     * @param status 按状态筛选（可选）
     */
    List<Order> listStoreOrders(String status);
}
