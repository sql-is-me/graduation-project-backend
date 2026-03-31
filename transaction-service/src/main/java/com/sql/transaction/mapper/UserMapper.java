package com.sql.transaction.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
