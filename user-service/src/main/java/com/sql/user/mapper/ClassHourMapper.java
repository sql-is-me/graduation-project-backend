package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.ClassHour;

@Mapper
public interface ClassHourMapper extends BaseMapper<ClassHour> {

    /**
     * 根据会员ID查询课时信息
     */
    @Select("SELECT * FROM class_hours WHERE user_id = #{vipId}")
    ClassHour selectByVIPId(@Param("vipId") Long vipId);
}
