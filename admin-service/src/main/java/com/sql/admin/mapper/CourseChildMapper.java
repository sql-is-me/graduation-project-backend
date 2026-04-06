package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.AttendanceRecord;

@Mapper
public interface CourseChildMapper extends BaseMapper<AttendanceRecord> {
}
