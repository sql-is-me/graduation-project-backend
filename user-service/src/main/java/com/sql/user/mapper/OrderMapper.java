package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}