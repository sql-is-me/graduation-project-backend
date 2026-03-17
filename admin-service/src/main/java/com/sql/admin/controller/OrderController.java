package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.db.Order;
import com.sql.common.enums.UserTypes;
import com.sql.admin.service.OrderService;
import com.sql.utils.BaseController;

/**
 * 管理员订单查询接口
 * admin可以查看系统全部订单
 * manager可以查看当前店铺订单
 */
@RestController
@RequestMapping("/admin/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    /**
     * 查询系统全部订单(仅顶级管理员)
     */
    @GetMapping("/all")
    @RequiresType(UserTypes.ADMIN)
    public TableDataInfo listAllOrders(@RequestParam(required = false) String status) {
        startPage();
        List<Order> list = orderService.listAllOrders(status);
        return getDataTable(list);
    }

    /**
     * 查询当前店铺订单(仅店铺管理员)
     */
    @GetMapping("/store")
    @RequiresType(UserTypes.MANAGER)
    public TableDataInfo listStoreOrders(@RequestParam(required = false) String status) {
        startPage();
        List<Order> list = orderService.listStoreOrders(status);
        return getDataTable(list);
    }
}