package com.sql.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.LoginInfo;

import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper接口
 */
@Mapper
public interface LoginInfoMapper extends BaseMapper<LoginInfo> {
}
