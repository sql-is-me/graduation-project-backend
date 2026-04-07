package com.sql.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.Coupon;
import com.sql.common.entity.po.UserCoupon;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.user.mapper.CouponMapper;
import com.sql.user.mapper.UserCouponMapper;
import com.sql.user.service.CouponService;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    public List<Coupon> listAvailableCoupons() {
        UserOnline uo = ContextHolder.getUO();
        Long storeId = uo.getUserInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("您尚未绑定店铺");
        }

        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStoreId, storeId)
                .eq(Coupon::getStatus, "0")
                .gt(Coupon::getRemainingCount, 0)
                .le(Coupon::getStartTime, now)
                .ge(Coupon::getEndTime, now)
                .orderByDesc(Coupon::getCreateTime);
        return couponMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public int claimCoupon(Long couponId) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();
        Long storeId = uo.getUserInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("您尚未绑定店铺");
        }

        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new ServiceException("优惠券不存在");
        }
        if (!coupon.getStoreId().equals(storeId)) {
            throw new ServiceException("该优惠券不属于您所在的店铺");
        }
        if (!"0".equals(coupon.getStatus())) {
            throw new ServiceException("该优惠券已停用");
        }
        if (coupon.getRemainingCount() <= 0) {
            throw new ServiceException("优惠券已领完");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new ServiceException("优惠券不在有效期内");
        }

        // 每人每张优惠券只能领取一次
        LambdaQueryWrapper<UserCoupon> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, couponId);
        if (userCouponMapper.selectCount(countWrapper) > 0) {
            throw new ServiceException("您已领取过该优惠券");
        }

        // 减少优惠券库存
        coupon.setRemainingCount(coupon.getRemainingCount() - 1);
        couponMapper.updateById(coupon);

        // 创建用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus("0"); // 未使用
        userCoupon.setClaimTime(LocalDateTime.now());

        return userCouponMapper.insert(userCoupon);
    }

    @Override
    @Transactional
    public int claimCouponByToken(String token) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();

        // 通过 token 查找优惠券
        LambdaQueryWrapper<Coupon> tokenWrapper = new LambdaQueryWrapper<>();
        tokenWrapper.eq(Coupon::getLinkToken, token);
        Coupon coupon = couponMapper.selectOne(tokenWrapper);
        if (coupon == null) {
            throw new ServiceException("活动链接无效或已失效");
        }
        if (!"0".equals(coupon.getStatus())) {
            throw new ServiceException("该优惠券已停用");
        }
        if (coupon.getRemainingCount() <= 0) {
            throw new ServiceException("优惠券已领完");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new ServiceException("优惠券不在有效期内");
        }

        // 每人每张优惠券只能领取一次
        LambdaQueryWrapper<UserCoupon> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, coupon.getCouponId());
        if (userCouponMapper.selectCount(countWrapper) > 0) {
            throw new ServiceException("您已领取过该优惠券");
        }

        // 减少优惠券库存
        coupon.setRemainingCount(coupon.getRemainingCount() - 1);
        couponMapper.updateById(coupon);

        // 创建用户优惠券记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(coupon.getCouponId());
        userCoupon.setStatus("0"); // 未使用
        userCoupon.setClaimTime(LocalDateTime.now());

        return userCouponMapper.insert(userCoupon);
    }

    @Override
    public List<UserCoupon> listMyCoupons(String status) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();

        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UserCoupon::getClaimTime);
        return userCouponMapper.selectList(wrapper);
    }
}
