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

import com.sql.admin.dto.CouponCreateDTO;
import com.sql.admin.service.CouponService;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.db.Coupon;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

/**
 * 优惠券发放管理
 */
@RestController
@RequestMapping("/admin/coupon")
@RequiresType(UserTypes.MANAGER)
public class CouponController extends BaseController {

    @Autowired
    private CouponService couponService;

    /**
     * 创建优惠券
     */
    @Log(title = "优惠券管理", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public R<?> createCoupon(@Validated @RequestBody CouponCreateDTO dto) {
        return R.ok(couponService.createCoupon(dto), "优惠券创建成功");
    }

    /**
     * 停用/启用优惠券
     */
    @Log(title = "优惠券管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{couponId}/toggle")
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
