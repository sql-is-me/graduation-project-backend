package com.sql.transaction.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.common.entity.db.Coupon;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.transaction.dto.CouponCreateDTO;
import com.sql.transaction.mapper.CouponMapper;
import com.sql.transaction.service.AdminCouponService;

@Service
public class AdminCouponServiceImpl implements AdminCouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public int createCoupon(CouponCreateDTO dto) {
        Long storeId = getStoreId();
        Long adminId = ContextHolder.getAO().getAdminInfo().getAdminId();

        if (dto.getEndTime().isBefore(dto.getStartTime())) {
            throw new ServiceException("结束时间不能早于开始时间");
        }

        if ("1".equals(dto.getCouponType())) {
            // 折扣券校验: 折扣值应在0到1之间(不含0和1)
            if (dto.getDiscountValue().doubleValue() <= 0 || dto.getDiscountValue().doubleValue() >= 1) {
                throw new ServiceException("折扣券的折扣值应在0到1之间(如0.8表示8折)");
            }
        }

        Coupon coupon = new Coupon();
        coupon.setCouponName(dto.getCouponName());
        coupon.setStoreId(storeId);
        coupon.setCreatorId(adminId);
        coupon.setCouponType(dto.getCouponType());
        coupon.setDiscountValue(dto.getDiscountValue());
        coupon.setMinAmount(dto.getMinAmount());
        coupon.setTotalCount(dto.getTotalCount());
        coupon.setRemainingCount(dto.getTotalCount());
        coupon.setClaimLimit(dto.getClaimLimit());
        coupon.setStartTime(dto.getStartTime());
        coupon.setEndTime(dto.getEndTime());
        coupon.setStatus("0");

        return couponMapper.insert(coupon);
    }

    @Override
    public int toggleCouponStatus(Long couponId) {
        Long storeId = getStoreId();
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new ServiceException("优惠券不存在");
        }
        if (!coupon.getStoreId().equals(storeId)) {
            throw new ServiceException("无权操作其他店铺的优惠券");
        }

        coupon.setStatus("0".equals(coupon.getStatus()) ? "1" : "0");
        return couponMapper.updateById(coupon);
    }

    @Override
    public List<Coupon> listCoupons() {
        Long storeId = getStoreId();
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStoreId, storeId)
                .orderByDesc(Coupon::getCreateTime);
        return couponMapper.selectList(wrapper);
    }

    @Override
    public Coupon getCouponDetail(Long couponId) {
        Long storeId = getStoreId();
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new ServiceException("优惠券不存在");
        }
        if (!coupon.getStoreId().equals(storeId)) {
            throw new ServiceException("无权查看其他店铺的优惠券");
        }
        return coupon;
    }

    private Long getStoreId() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }
        return storeId;
    }
}
