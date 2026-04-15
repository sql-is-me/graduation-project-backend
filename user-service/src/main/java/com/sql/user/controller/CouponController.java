package com.sql.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.entity.vo.UserCouponInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.service.CouponService;
import com.sql.utils.BaseController;

/**
 * 会员优惠券领取接口
 */
@RestController
@RequestMapping("/user/coupon")
@LoginRequired
@RequiresType(UserTypes.VIP)
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 查询可领取的优惠券列表
     */
    @GetMapping("/available")
    public TableDataInfo listAvailableCoupons() {
        startPage();
        List<Coupon> list = couponService.listAvailableCoupons();
        return getDataTable(list);
    }

    /**
     * 店铺详情页领取优惠券（需绑定店铺）
     */
    @Log(title = "优惠券领取", businessType = BusinessType.INSERT)
    @PostMapping("/claim/{couponId}")
    public R<?> claimCoupon(@PathVariable Long couponId) {
        return R.ok(couponService.claimCoupon(couponId), "优惠券领取成功");
    }

    /**
     * 活动链接领取优惠券（通过 token，无需绑定店铺）
     */
    @Log(title = "优惠券领取", businessType = BusinessType.INSERT)
    @PostMapping("/claim/link/{token}")
    public R<?> claimCouponByToken(@PathVariable String token) {
        return R.ok(couponService.claimCouponByToken(token), "优惠券领取成功");
    }

    /**
     * 查询我的优惠券列表
     */
    @GetMapping("/my")
    public TableDataInfo listMyCoupons(@RequestParam(required = false) String status) {
        startPage();
        List<UserCouponInfo> list = couponService.listMyCoupons(status);
        return getDataTable(list);
    }

    /**
     * 查询优惠券详情
     */
    @GetMapping("/{couponId}")
    public Coupon getCoupon(@PathVariable Long couponId) {
        return couponService.getCoupon(couponId);
    }
}
