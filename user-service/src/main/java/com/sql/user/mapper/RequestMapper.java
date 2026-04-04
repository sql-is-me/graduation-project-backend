package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Request;

import java.util.List;

@Mapper
public interface RequestMapper extends BaseMapper<Request> {

    @Select("SELECT * FROM requests WHERE sender_id = #{senderId} ORDER BY create_time DESC")
    List<Request> selectBySenderId(@Param("senderId") Long senderId);

    @Select("SELECT * FROM requests WHERE approver1_id = #{approverId} AND approver1_status = '0' ORDER BY create_time DESC")
    List<Request> selectPendingByApprover1(@Param("approverId") Long approverId);
}
