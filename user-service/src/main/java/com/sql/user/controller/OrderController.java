package com.sql.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.db.Order;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.user.service.OrderService;
import com.sql.utils.BaseController;

/**
 * VIP会员订单查询接口
 */
@RestController
@RequestMapping("/user/order")
@RequiresType(UserTypes.VIP)
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    /**
     * 查询我的订单列表
     */
    @GetMapping("/my")
    public TableDataInfo listMyOrders(@RequestParam(required = false) String status) {
        startPage();
        List<Order> list = orderService.listMyOrders(status);
        return getDataTable(list);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/{orderId}")
    public R<?> getOrderDetail(@PathVariable Long orderId) {
        return R.ok(orderService.getOrderDetail(orderId));
    }
}