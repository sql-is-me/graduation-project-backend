package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.db.Admin;

/**
 * 管理员用户Mapper接口
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
    @Select("SELECT * FROM admins WHERE username = #{username}")
    Admin selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM admins WHERE phone = #{phone}")
    Admin selectByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM admins WHERE email = #{email}")
    Admin selectByEmail(@Param("email") String email);

    @Update("UPDATE admins SET password = #{password} WHERE admin_id = #{adminId}")
    int updatePassword(@Param("adminId") Long adminId, @Param("password") String password);

    @Update("UPDATE admins SET avatar = #{avatar} WHERE admin_id = #{adminId}")
    int updateAvatar(@Param("adminId") Long adminId, @Param("avatar") String avatar);
}
