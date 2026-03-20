package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.db.User;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User checkPhoneUnique(@Param("phone") String phone);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    @Update("UPDATE users SET password = #{password}, update_time = now() WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    @Update("UPDATE users SET avatar = #{avatar}, update_time = now() WHERE user_id = #{userId}")
    int updateAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);
}
