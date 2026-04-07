package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Coupon;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}
