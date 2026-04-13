package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.ClassHour;

@Mapper
public interface ClassHourMapper extends BaseMapper<ClassHour> {

    @Select("SELECT * FROM class_hour WHERE user_id = #{userId}")
    ClassHour getClassHourByUserId(@Param("userId") Long userId);
}
