package com.sql.admin.service;

import java.util.List;

import com.sql.admin.dto.CouponCreateDTO;
import com.sql.common.entity.db.Coupon;

public interface CouponService {

    /**
     * 创建优惠券(manager)
     */
    int createCoupon(CouponCreateDTO dto);

    /**
     * 停用/启用优惠券(manager)
     */
    int toggleCouponStatus(Long couponId);

    /**
     * 查询当前店铺优惠券列表(manager)
     */
    List<Coupon> listCoupons();

    /**
     * 查询优惠券详情(manager)
     */
    Coupon getCouponDetail(Long couponId);
}
