package com.sql.user.service;

import java.util.List;

import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.vo.UserCouponInfo;

public interface CouponService {

    /**
     * 查询当前店铺可领取的优惠券列表
     */
    List<Coupon> listAvailableCoupons();

    /**
     * 店铺详情页领取优惠券（需绑定店铺）
     */
    int claimCoupon(Long couponId);

    /**
     * 活动链接领取优惠券（无需绑定店铺，通过 token 匹配）
     */
    int claimCouponByToken(String token);

    /**
     * 查询我的优惠券列表
     */
    List<UserCouponInfo> listMyCoupons(String status);

    /**
     * 查询优惠券详情
     */
    Coupon getCoupon(Long couponId);
}
