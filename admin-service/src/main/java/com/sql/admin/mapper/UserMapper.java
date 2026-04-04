package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Update("UPDATE users SET status = #{status} WHERE user_id = #{userId}")
    int updateStatus(Long userId, String status);
}
