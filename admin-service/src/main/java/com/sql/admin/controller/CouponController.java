package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.CouponService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.dto.CouponCreateDTO;
import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

/**
 * 店铺优惠券管理（MANAGER端）
 */
@RestController
@RequestMapping("/admin/coupon")
@LoginRequired
@RequiresType(UserTypes.MANAGER)
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 创建优惠券
     * 可在 linkToken 字段设置活动链接Token，使该券支持通过活动链接领取
     */
    @Log(title = "优惠券管理", businessType = BusinessType.INSERT, operatorType = UserTypes.MANAGER)
    @PostMapping
    public R<?> createCoupon(@Validated @RequestBody CouponCreateDTO dto) {
        return R.ok(couponService.createCoupon(dto), "优惠券创建成功");
    }

    /**
     * 停用/启用优惠券
     */
    @Log(title = "优惠券管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @PutMapping("/{couponId}/status")
    public R<?> toggleCouponStatus(@PathVariable Long couponId) {
        return R.ok(couponService.toggleCouponStatus(couponId), "操作成功");
    }

    /**
     * 查询当前店铺优惠券列表
     */
    @GetMapping("/list")
    public TableDataInfo listCoupons() {
        startPage();
        List<Coupon> list = couponService.listCoupons();
        return getDataTable(list);
    }

    /**
     * 查询优惠券详情
     */
    @GetMapping("/{couponId}")
    public R<?> getCouponDetail(@PathVariable Long couponId) {
        return R.ok(couponService.getCouponDetail(couponId));
    }
}
