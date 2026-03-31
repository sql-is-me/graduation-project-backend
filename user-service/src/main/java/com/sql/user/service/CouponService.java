package com.sql.user.service;

import java.util.List;

import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.po.UserCoupon;

public interface CouponService {

    /**
     * 查询当前店铺可领取的优惠券列表
     */
    List<Coupon> listAvailableCoupons();

    /**
     * 领取优惠券
     */
    int claimCoupon(Long couponId);

    /**
     * 查询我的优惠券列表
     */
    List<UserCoupon> listMyCoupons(String status);
}
