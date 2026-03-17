package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.db.Coupon;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}
