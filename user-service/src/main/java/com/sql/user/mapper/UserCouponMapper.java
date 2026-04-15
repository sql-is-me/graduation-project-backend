package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.UserCoupon;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    /**
     * 在事务内对该用户+优惠券的记录加行锁（FOR UPDATE），
     * 并发请求会在此处串行排队，防止超过 claimLimit 的超领。
     * 调用方必须处于 @Transactional 事务中。
     */
    @Select("SELECT COUNT(*) FROM user_coupons " +
            "WHERE user_id = #{userId} AND coupon_id = #{couponId} FOR UPDATE")
    long countForUpdate(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
