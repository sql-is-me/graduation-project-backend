package com.sql.transaction.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.Order;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
import com.sql.transaction.dto.WechatPayCallbackDTO;
import com.sql.transaction.service.OrderService;
import com.sql.utils.BaseController;

/**
 * 会员订单接口
 * 负责订单创建、取消、支付、查询
 */
@RestController
@RequestMapping("/transaction/order")
@LoginRequired
@RequiresType(UserTypes.VIP)
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    // ─────────────── 创建 ───────────────

    /**
     * 创建订单
     * productType=0：按量购买课时（1元/课时）
     * productType=1：套餐购买（packageType: p10/p30/p50，含折扣）
     */
    @Log(title = "订单管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public R<?> createOrder(@Validated @RequestBody OrderCreateDTO dto) {
        return R.ok(orderService.createOrder(dto), "订单创建成功");
    }

    // ─────────────── 取消 ───────────────

    /**
     * 取消订单（仅待支付状态可取消）
     */
    @Log(title = "订单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{orderId}/cancel")
    public R<?> cancelOrder(@PathVariable Long orderId,
            @RequestBody(required = false) OrderCancelDTO dto) {
        orderService.cancelOrder(orderId, dto);
        return R.ok("订单已取消");
    }

    // ─────────────── 查询 ───────────────

    /**
     * 查询我的订单列表
     * status: 0-待支付 1-已支付 2-已取消 3-已退款（不传则查全部）
     */
    @GetMapping("/my")
    public TableDataInfo listMyOrders(@RequestParam(required = false) String status) {
        startPage();
        List<Order> list = orderService.listMyOrders(status);
        return getDataTable(list);
    }

    /**
     * 查询单笔订单详情
     */
    @GetMapping("/{orderId}")
    public R<?> getMyOrder(@PathVariable Long orderId) {
        return R.ok(orderService.getMyOrder(orderId));
    }

    // ─────────────── 支付 ───────────────

    /**
     * 模拟支付（演示用）
     * 直接将订单置为已支付并到账课时，无需走微信支付流程
     */
    @Log(title = "模拟支付", businessType = BusinessType.OTHER)
    @PostMapping("/{orderId}/pay/mock")
    public R<?> mockPay(@PathVariable Long orderId) {
        orderService.mockPay(orderId);
        return R.ok("支付成功，课时已到账");
    }

    /**
     * 发起微信支付
     * 返回微信小程序调起支付所需参数，前端拿到后调用 wx.requestPayment
     */
    @Log(title = "微信支付", businessType = BusinessType.OTHER)
    @PostMapping("/{orderId}/pay/wechat")
    public R<?> prepayWechat(@PathVariable Long orderId) {
        return R.ok(orderService.prepayWechat(orderId), "预支付参数获取成功");
    }

    /**
     * 微信支付回调通知（无需鉴权，由微信服务器调用）
     */
    @PostMapping("/pay/wechat/callback")
    public String wechatPayCallback(@RequestBody WechatPayCallbackDTO dto) {
        return orderService.wechatPayCallback(dto);
    }
}
