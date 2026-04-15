package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Coupon;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    /**
     * 原子扣减库存，仅当 remaining_count > 0 时才更新，返回影响行数。
     * 返回 0 表示库存已耗尽（乐观锁防超扣）。
     */
    @Update("UPDATE coupons SET remaining_count = remaining_count - 1 " +
            "WHERE coupon_id = #{couponId} AND remaining_count > 0")
    int decrementRemainingCount(Long couponId);
}
