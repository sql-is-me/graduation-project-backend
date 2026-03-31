package com.sql.transaction.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sql.api.RemoteClassHourService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.po.Order;
import com.sql.common.entity.po.UserCoupon;
import com.sql.common.entity.result.R;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
import com.sql.transaction.dto.WechatPayCallbackDTO;
import com.sql.transaction.mapper.CouponMapper;
import com.sql.transaction.mapper.OrderMapper;
import com.sql.transaction.mapper.UserCouponMapper;
import com.sql.transaction.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    /** 课时单价: 1元 = 1课时 */
    private static final BigDecimal UNIT_PRICE = BigDecimal.ONE;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private RemoteClassHourService remoteClassHourService;

    @Override
    @Transactional
    public Order createOrder(OrderCreateDTO dto) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();
        Long storeId = uo.getUserInfo().getStoreId();

        if (storeId == null) {
            throw new ServiceException("您尚未绑定店铺，无法购买课时");
        }

        int quantity = dto.getQuantity();
        BigDecimal totalAmount = UNIT_PRICE.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discountAmount = BigDecimal.ZERO;

        // 处理优惠券
        if (dto.getUserCouponId() != null) {
            discountAmount = applyCoupon(dto.getUserCouponId(), userId, totalAmount);
        }

        BigDecimal payAmount = totalAmount.subtract(discountAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStoreId(storeId);
        order.setProductType("0"); // 课时购买
        order.setQuantity(quantity);
        order.setUnitPrice(UNIT_PRICE);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setCouponId(dto.getUserCouponId());
        order.setStatus("0"); // 待支付

        orderMapper.insert(order);
        return order;
    }

    @Override
    @Transactional
    public int cancelOrder(Long orderId, OrderCancelDTO dto) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此订单");
        }
        if (!"0".equals(order.getStatus())) {
            throw new ServiceException("仅待支付状态的订单可取消");
        }

        // 归还优惠券
        if (order.getCouponId() != null) {
            LambdaUpdateWrapper<UserCoupon> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(UserCoupon::getUserCouponId, order.getCouponId())
                    .set(UserCoupon::getStatus, "0")
                    .set(UserCoupon::getUsedOrderId, null)
                    .set(UserCoupon::getUsedTime, null);
            userCouponMapper.update(null, wrapper);
        }

        order.setStatus("2"); // 已取消
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(dto != null ? dto.getCancelReason() : null);
        return orderMapper.updateById(order);
    }

    @Override
    public Object prepayWechat(Long orderId) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此订单");
        }
        if (!"0".equals(order.getStatus())) {
            throw new ServiceException("该订单状态不允许支付");
        }

        // TODO: 实际接入微信支付SDK，调用统一下单接口
        // 此处返回模拟的预支付参数，待接入微信支付后替换
        Map<String, String> prepayParams = new HashMap<>();
        prepayParams.put("appId", "wx_appid_placeholder");
        prepayParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        prepayParams.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
        prepayParams.put("package", "prepay_id=wx_prepay_placeholder_" + order.getOrderNo());
        prepayParams.put("signType", "RSA");
        prepayParams.put("paySign", "sign_placeholder");
        prepayParams.put("orderNo", order.getOrderNo());
        prepayParams.put("payAmount", order.getPayAmount().toString());

        // 更新支付方式
        order.setPayType("wechat");
        orderMapper.updateById(order);

        return prepayParams;
    }

    @Override
    @Transactional
    public String wechatPayCallback(WechatPayCallbackDTO dto) {
        // TODO: 实际接入时需要验证微信签名
        if (!"SUCCESS".equals(dto.getResultCode())) {
            return "FAIL";
        }

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, dto.getOrderNo());
        Order order = orderMapper.selectOne(wrapper);

        if (order == null) {
            return "FAIL";
        }
        if (!"0".equals(order.getStatus())) {
            // 已处理过，直接返回成功
            return "SUCCESS";
        }

        // 更新订单状态为已支付
        order.setStatus("1");
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId(dto.getTransactionId());
        orderMapper.updateById(order);

        // 标记优惠券已使用
        if (order.getCouponId() != null) {
            LambdaUpdateWrapper<UserCoupon> ucWrapper = new LambdaUpdateWrapper<>();
            ucWrapper.eq(UserCoupon::getUserCouponId, order.getCouponId())
                    .set(UserCoupon::getStatus, "1")
                    .set(UserCoupon::getUsedOrderId, order.getOrderId())
                    .set(UserCoupon::getUsedTime, LocalDateTime.now());
            userCouponMapper.update(null, ucWrapper);
        }

        // 通过内部调用admin-service增加课时
        R<Boolean> result = remoteClassHourService.addClassHours(
                order.getUserId(), order.getQuantity(), AuthConstants.INNER);
        if (result == null || !R.isSuccess(result)) {
            throw new ServiceException("增加课时失败，请联系管理员");
        }

        return "SUCCESS";
    }

    // ============ 私有方法 ============

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return "LS" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    /**
     * 应用优惠券，返回优惠金额
     */
    private BigDecimal applyCoupon(Long userCouponId, Long userId, BigDecimal totalAmount) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            throw new ServiceException("优惠券不存在");
        }
        if (!userCoupon.getUserId().equals(userId)) {
            throw new ServiceException("该优惠券不属于当前用户");
        }
        if (!"0".equals(userCoupon.getStatus())) {
            throw new ServiceException("该优惠券已使用或已过期");
        }

        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null) {
            throw new ServiceException("优惠券模板不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new ServiceException("优惠券不在有效期内");
        }

        if (totalAmount.compareTo(coupon.getMinAmount()) < 0) {
            throw new ServiceException("订单金额未达到优惠券使用门槛(满" + coupon.getMinAmount() + "元可用)");
        }

        BigDecimal discount;
        if ("0".equals(coupon.getCouponType())) {
            // 满减券
            discount = coupon.getDiscountValue();
        } else {
            // 折扣券: 优惠金额 = 总金额 * (1 - 折扣比例)
            discount = totalAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountValue()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 锁定优惠券（标记使用中，支付回调时确认）
        userCoupon.setStatus("1");
        userCoupon.setUsedTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);

        return discount;
    }
}
