package com.sql.transaction.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.po.Order;
import com.sql.common.entity.po.UserCoupon;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.transaction.constants.PackageType;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
import com.sql.transaction.dto.WechatPayCallbackDTO;
import com.sql.transaction.mapper.CouponMapper;
import com.sql.transaction.mapper.OrderMapper;
import com.sql.transaction.mapper.UserCouponMapper;
import com.sql.transaction.service.ClassHourService;
import com.sql.transaction.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    /** 单课时单价：1元 = 1课时 */
    private static final BigDecimal UNIT_PRICE = BigDecimal.ONE;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private ClassHourService classHourService;

    @Override
    @Transactional
    public Order createOrder(OrderCreateDTO dto) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();
        Long storeId = uo.getUserInfo().getStoreId();

        if (storeId == null) {
            throw new ServiceException("您尚未绑定店铺，无法购买课时");
        }

        int quantity;
        BigDecimal totalAmount;
        BigDecimal discountAmount = BigDecimal.ZERO;
        String productType = dto.getProductType();

        if ("1".equals(productType)) {
            // 套餐购买：课时数和原价由套餐决定，套餐本身已含折扣
            String pkg = dto.getPackageType();
            if (pkg == null) {
                throw new ServiceException("未指定套餐类型");
            }
            quantity = resolvePackageHours(pkg);
            BigDecimal packagePrice = resolvePackagePrice(pkg);
            totalAmount = UNIT_PRICE.multiply(BigDecimal.valueOf(quantity));
            discountAmount = totalAmount.subtract(packagePrice);
        } else {
            // 单课时购买
            if (dto.getQuantity() == null || dto.getQuantity() < 1) {
                throw new ServiceException("购买课时数不能为空且至少为1");
            }
            quantity = dto.getQuantity();
            totalAmount = UNIT_PRICE.multiply(BigDecimal.valueOf(quantity));
        }

        // 再叠加优惠券折扣
        if (dto.getUserCouponId() != null) {
            BigDecimal couponDiscount = applyCoupon(dto.getUserCouponId(), userId,
                    totalAmount.subtract(discountAmount));
            discountAmount = discountAmount.add(couponDiscount);
        }

        BigDecimal payAmount = totalAmount.subtract(discountAmount)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStoreId(storeId);
        order.setProductType(productType);
        order.setQuantity(quantity);
        order.setUnitPrice(UNIT_PRICE);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setCouponId(dto.getUserCouponId());
        order.setStatus("0"); // 待支付

        int rows = orderMapper.insert(order);
        if (rows <= 0) {
            throw new ServiceException("创建订单失败，请联系工作人员");
        }

        return order;
    }

    @Override
    @Transactional
    public int cancelOrder(Long orderId, OrderCancelDTO dto) {
        Order order = getMyOrderById(orderId);

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
    public List<Order> listMyOrders(String status) {
        Long userId = ContextHolder.getUO().getUserInfo().getUserId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null && !status.isBlank()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Order getMyOrder(Long orderId) {
        return getMyOrderById(orderId);
    }

    @Override
    @Transactional
    public void mockPay(Long orderId) {
        Order order = getMyOrderById(orderId);

        if (!"0".equals(order.getStatus())) {
            throw new ServiceException("该订单状态不允许支付");
        }

        order.setStatus("1"); // 已支付
        order.setPayType("mock");
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId("MOCK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        orderMapper.updateById(order);

        confirmCouponUsed(order);
        creditClassHours(order.getUserId(), order.getQuantity());
    }

    @Override
    public Object prepayWechat(Long orderId) {
        Order order = getMyOrderById(orderId);

        if (!"0".equals(order.getStatus())) {
            throw new ServiceException("该订单状态不允许支付");
        }

        // TODO: 接入微信支付 SDK，调用统一下单接口，替换下方模拟参数
        Map<String, String> prepayParams = new HashMap<>();
        prepayParams.put("appId", "wx_appid_placeholder");
        prepayParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        prepayParams.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
        prepayParams.put("package", "prepay_id=wx_prepay_placeholder_" + order.getOrderNo());
        prepayParams.put("signType", "RSA");
        prepayParams.put("paySign", "sign_placeholder");
        prepayParams.put("orderNo", order.getOrderNo());
        prepayParams.put("payAmount", order.getPayAmount().toString());

        order.setPayType("wechat");
        orderMapper.updateById(order);

        return prepayParams;
    }

    @Override
    @Transactional
    public String wechatPayCallback(WechatPayCallbackDTO dto) {
        // TODO: 接入时需验证微信签名
        if (!"SUCCESS".equals(dto.getResultCode())) {
            return "FAIL";
        }

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, dto.getOrderNo());
        Order order = orderMapper.selectOne(wrapper);

        if (order == null)
            return "FAIL";
        if (!"0".equals(order.getStatus()))
            return "SUCCESS"; // 幂等

        order.setStatus("1");
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId(dto.getTransactionId());
        orderMapper.updateById(order);

        confirmCouponUsed(order);
        creditClassHours(order.getUserId(), order.getQuantity());

        return "SUCCESS";
    }

    // ============ 私有方法 ============

    private Order getMyOrderById(Long orderId) {
        Long userId = ContextHolder.getUO().getUserInfo().getUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ServiceException("无权操作此订单");
        }
        return order;
    }

    private void confirmCouponUsed(Order order) {
        if (order.getCouponId() == null)
            return;
        LambdaUpdateWrapper<UserCoupon> ucWrapper = new LambdaUpdateWrapper<>();
        ucWrapper.eq(UserCoupon::getUserCouponId, order.getCouponId())
                .set(UserCoupon::getStatus, "1")
                .set(UserCoupon::getUsedOrderId, order.getOrderId())
                .set(UserCoupon::getUsedTime, LocalDateTime.now());
        userCouponMapper.update(null, ucWrapper);
    }

    private void creditClassHours(Long userId, int hours) {
        int rows = classHourService.addClassHours(userId, hours);
        if (rows <= 0) {
            throw new ServiceException("课时到账失败，请联系管理员");
        }
    }

    private String generateOrderNo() {
        return "LS" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    private BigDecimal applyCoupon(Long userCouponId, Long userId, BigDecimal effectiveAmount) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null)
            throw new ServiceException("优惠券不存在");
        if (!userCoupon.getUserId().equals(userId))
            throw new ServiceException("该优惠券不属于当前用户");
        if (!"0".equals(userCoupon.getStatus()))
            throw new ServiceException("该优惠券已使用或已过期");

        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null)
            throw new ServiceException("优惠券模板不存在");

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new ServiceException("优惠券不在有效期内");
        }
        if (effectiveAmount.compareTo(coupon.getMinAmount()) < 0) {
            throw new ServiceException("订单金额未达到优惠券使用门槛（满" + coupon.getMinAmount() + "元可用）");
        }

        BigDecimal discount;
        if ("0".equals(coupon.getCouponType())) {
            discount = coupon.getDiscountValue();
        } else {
            discount = effectiveAmount
                    .multiply(BigDecimal.ONE.subtract(coupon.getDiscountValue()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // 锁定优惠券（支付回调时确认）
        userCoupon.setStatus("1");
        userCoupon.setUsedTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);

        return discount;
    }

    private int resolvePackageHours(String pkg) {
        return switch (pkg) {
            case PackageType.P10 -> PackageType.P10_HOURS;
            case PackageType.P30 -> PackageType.P30_HOURS;
            case PackageType.P50 -> PackageType.P50_HOURS;
            default -> throw new ServiceException("无效的套餐类型：" + pkg);
        };
    }

    private BigDecimal resolvePackagePrice(String pkg) {
        return switch (pkg) {
            case PackageType.P10 -> PackageType.P10_PRICE;
            case PackageType.P30 -> PackageType.P30_PRICE;
            case PackageType.P50 -> PackageType.P50_PRICE;
            default -> throw new ServiceException("无效的套餐类型：" + pkg);
        };
    }
}
