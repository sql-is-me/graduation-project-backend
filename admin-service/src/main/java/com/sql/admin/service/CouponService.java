package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.dto.CouponCreateDTO;
import com.sql.common.entity.po.Coupon;

public interface CouponService {

    /**
     * 创建优惠券（可选配置活动链接Token）
     */
    long createCoupon(CouponCreateDTO dto);

    /**
     * 停用/启用优惠券
     */
    int toggleCouponStatus(Long couponId);

    /**
     * 查询当前店铺的优惠券列表
     */
    List<Coupon> listCoupons();

    /**
     * 查询优惠券详情
     */
    Coupon getCouponDetail(Long couponId);
}
