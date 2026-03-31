package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Court;

@Mapper
public interface CourtMapper extends BaseMapper<Court> {
}
