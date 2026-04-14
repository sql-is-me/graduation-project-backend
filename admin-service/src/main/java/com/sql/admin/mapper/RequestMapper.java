package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Request;

@Mapper
public interface RequestMapper extends BaseMapper<Request> {

    /**
     * 更新 approver1 状态
     */
    @Update("UPDATE requests SET approver1_status = #{status}, update_time = now() WHERE request_id = #{requestId}")
    int updateApprover1Status(@Param("requestId") Long requestId, @Param("status") String status);

    /**
     * 更新 approver2 状态
     */
    @Update("UPDATE requests SET approver2_status = #{status}, update_time = now() WHERE request_id = #{requestId}")
    int updateApprover2Status(@Param("requestId") Long requestId, @Param("status") String status);

    /**
     * 更新整体状态
     */
    @Update("UPDATE requests SET status = #{status}, reject_reason = #{rejectReason}, update_time = now() WHERE request_id = #{requestId}")
    int updateOverallStatus(@Param("requestId") Long requestId, @Param("status") String status, @Param("rejectReason") String rejectReason);
}
