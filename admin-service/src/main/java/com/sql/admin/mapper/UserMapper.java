package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.db.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
