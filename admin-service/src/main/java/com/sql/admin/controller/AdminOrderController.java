package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.AdminOrderService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.Order;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.utils.BaseController;

/**
 * 管理员订单查询接口
 */
@RestController
@RequestMapping("/admin/order")
@LoginRequired
public class AdminOrderController extends BaseController {

    @Autowired
    private AdminOrderService adminOrderService;

    /**
     * 系统管理员查询全部订单
     * 支持按 storeId（店铺）和 status（状态）筛选
     */
    @GetMapping("/all")
    @RequiresType(UserTypes.ADMIN)
    public TableDataInfo listAllOrders(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String status) {
        startPage();
        List<Order> list = adminOrderService.listAllOrders(storeId, status);
        return getDataTable(list);
    }

    /**
     * 店铺管理员查询本店铺订单
     * 支持按 status（状态）筛选
     */
    @GetMapping("/store")
    @RequiresType(UserTypes.MANAGER)
    public TableDataInfo listStoreOrders(
            @RequestParam(required = false) String status) {
        startPage();
        List<Order> list = adminOrderService.listStoreOrders(status);
        return getDataTable(list);
    }
}
