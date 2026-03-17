package com.sql.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
import com.sql.transaction.dto.WechatPayCallbackDTO;
import com.sql.transaction.service.OrderService;
import com.sql.utils.BaseController;

/**
 * 交易服务接口
 * 负责订单创建、取消、微信支付
 * 订单查询已移至各自微服务(user-service/admin-service)
 */
@RestController
@RequestMapping("/transaction/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建课时购买订单
     * 1人民币 = 1课时
     */
    @Log(title = "订单管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    @RequiresType(UserTypes.VIP)
    public R<?> createOrder(@Validated @RequestBody OrderCreateDTO dto) {
        return R.ok(orderService.createOrder(dto), "订单创建成功");
    }

    /**
     * 取消订单(仅待支付状态可取消)
     */
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{orderId}/cancel")
    @RequiresType(UserTypes.VIP)
    public R<?> cancelOrder(@PathVariable Long orderId, @RequestBody(required = false) OrderCancelDTO dto) {
        return R.ok(orderService.cancelOrder(orderId, dto), "订单取消成功");
    }

    /**
     * 发起微信支付
     * 返回微信小程序调起支付所需参数
     */
    @Log(title = "微信支付", businessType = BusinessType.OTHER)
    @PostMapping("/{orderId}/pay/wechat")
    @RequiresType(UserTypes.VIP)
    public R<?> prepayWechat(@PathVariable Long orderId) {
        return R.ok(orderService.prepayWechat(orderId), "预支付参数获取成功");
    }

    /**
     * 微信支付回调通知
     * 此接口无需鉴权，由微信服务器调用
     */
    @PostMapping("/pay/wechat/callback")
    public String wechatPayCallback(@RequestBody WechatPayCallbackDTO dto) {
        return orderService.wechatPayCallback(dto);
    }
}