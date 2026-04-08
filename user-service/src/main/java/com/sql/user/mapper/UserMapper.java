package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.User;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM users WHERE openId = #{openId}")
    User selectByOpenId(@Param("openId") String openId);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    @Update("UPDATE users SET password = #{password}, update_time = now() WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    @Update("UPDATE users SET avatar = #{avatar}, update_time = now() WHERE user_id = #{userId}")
    int updateAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    @Update("UPDATE users SET photo = #{photo}, update_time = now() WHERE user_id = #{userId}")
    int updatePhoto(@Param("userId") Long userId, @Param("photo") String photo);
}
