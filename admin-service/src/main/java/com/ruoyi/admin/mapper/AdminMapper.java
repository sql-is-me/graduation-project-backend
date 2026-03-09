package com.ruoyi.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.ruoyi.common.entity.Admin;

/**
 * 管理员用户Mapper接口
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    @Select("SELECT * FROM users WHERE username = #{username}")
    Admin selectByUsername(@Param("username") String username);
}
