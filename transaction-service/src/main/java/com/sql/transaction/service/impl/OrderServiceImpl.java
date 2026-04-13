package com.sql.transaction.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.po.Order;
import com.sql.common.entity.po.UserCoupon;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.transaction.constants.PackageType;
import com.sql.transaction.dto.OrderCancelDTO;
import com.sql.transaction.dto.OrderCreateDTO;
import com.sql.transaction.mapper.ClassHourMapper;
import com.sql.transaction.mapper.CouponMapper;
import com.sql.transaction.mapper.OrderMapper;
import com.sql.transaction.mapper.UserCouponMapper;
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
    private ClassHourMapper classHourMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${server.port}")
    private int serverPort;

    /** 模拟微信支付服务地址（本服务内部） */
    private String mockPayBase() {
        return "http://localhost:" + serverPort + "/mock/wechat/pay";
    }

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

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public Object prepay(Long orderId) {
        Order order = getMyOrderById(orderId);

        if (!"0".equals(order.getStatus())) {
            throw new ServiceException("该订单状态不允许支付");
        }

        // 调用模拟微信统一下单接口
        Map<String, Object> request = new HashMap<>();
        request.put("out_trade_no", order.getOrderNo());
        // request.put("description", "课时购买-" + order.getQuantity() + "课时");
        request.put("total", order.getPayAmount().multiply(BigDecimal.valueOf(100)).intValue()); // 元转分

        Map<String, Object> response = restTemplate.postForObject(
                mockPayBase() + "/unified-order", request, Map.class);

        if (response == null || !"SUCCESS".equals(response.get("code"))) {
            throw new ServiceException("预支付失败：" + (response != null ? response.get("message") : "无响应"));
        }

        // 记录 prepay_id 和支付方式
        String prepayId = (String) response.get("prepay_id");
        order.setPayType("wechat");
        order.setTransactionId(prepayId); // 暂存 prepay_id，确认支付后更新为真实交易号
        orderMapper.updateById(order);

        // 返回前端调起支付所需参数
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("payAmount", order.getPayAmount());
        result.put("prepay_id", prepayId);
        result.put("pay_params", response.get("pay_params"));
        return result;
    }

    @Override
    @Transactional
    public void confirmPay(Long orderId) {
        Order order = getMyOrderById(orderId);

        if (!"0".equals(order.getStatus())) {
            throw new ServiceException("该订单状态不允许支付");
        }

        // 调用模拟微信订单查询接口，验证支付结果（对应微信官方按商户订单号查单）
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(
                mockPayBase() + "/query-order/" + order.getOrderNo(), Map.class);

        if (response == null || !"SUCCESS".equals(response.get("code"))) {
            throw new ServiceException("查询支付状态失败");
        }

        String tradeState = (String) response.get("trade_state");
        if (!"SUCCESS".equals(tradeState)) {
            throw new ServiceException("支付未完成，当前状态：" + response.get("trade_state_desc"));
        }

        // 支付成功，更新订单
        String transactionId = (String) response.get("transaction_id");
        order.setStatus("1"); // 已支付
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId(transactionId);
        orderMapper.updateById(order);

        confirmCouponUsed(order);
        creditClassHours(order.getUserId(), order.getQuantity());
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

    @Transactional
    private void creditClassHours(Long userId, int hours) {
        LambdaQueryWrapper<ClassHour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassHour::getUserId, userId);
        ClassHour classHour = classHourMapper.selectOne(wrapper);

        classHour.setHours(classHour.getHours() + hours);
        classHour.setRemainingHours(classHour.getRemainingHours() + hours);

        int rows = classHourMapper.updateById(classHour);
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
