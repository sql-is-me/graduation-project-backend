package com.sql.common.entity.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.po.UserCoupon;

import lombok.Data;

/**
 * 我的优惠券展示 VO
 * 聚合 user_coupons 与 coupons 表信息，供用户端展示使用
 */
@Data
public class UserCouponInfo {

    /** 用户优惠券ID */
    private Long userCouponId;

    /** 优惠券模板ID */
    private Long couponId;

    /** 优惠券名称 */
    private String couponName;

    /**
     * 优惠券类型
     * 0-满减券 1-折扣券
     */
    private String couponType;

    /**
     * 优惠值
     * 满减券：减免金额；折扣券：0-1 之间的折扣比例（如 0.95 表示 9.5 折）
     */
    private BigDecimal discountValue;

    /** 最低消费金额（满减门槛） */
    private BigDecimal minAmount;

    /** 生效开始时间 */
    private LocalDateTime startTime;

    /** 生效结束时间 */
    private LocalDateTime endTime;

    /** 领取时间 */
    private LocalDateTime claimTime;

    /** 使用时间 */
    private LocalDateTime usedTime;

    /**
     * 状态
     * 0-未使用 1-已使用 2-已过期
     * 若数据库记录为未使用但已超过 endTime，这里会自动展示为已过期
     */
    private String status;

    public UserCouponInfo() {
    }

    public UserCouponInfo(UserCoupon uc, Coupon coupon) {
        this.userCouponId = uc.getUserCouponId();
        this.couponId = uc.getCouponId();
        this.claimTime = uc.getClaimTime();
        this.usedTime = uc.getUsedTime();

        if (coupon != null) {
            this.couponName = coupon.getCouponName();
            this.couponType = coupon.getCouponType();
            this.discountValue = coupon.getDiscountValue();
            this.minAmount = coupon.getMinAmount();
            this.startTime = coupon.getStartTime();
            this.endTime = coupon.getEndTime();
        }

        // 展示状态：未使用但已过期 -> 2；其他沿用数据库状态
        String dbStatus = uc.getStatus();
        if ("0".equals(dbStatus) && this.endTime != null
                && LocalDateTime.now().isAfter(this.endTime)) {
            this.status = "2";
        } else {
            this.status = dbStatus;
        }
    }
}
