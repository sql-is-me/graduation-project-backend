package com.sql.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Request;

@Mapper
public interface RequestMapper extends BaseMapper<Request> {

    /**
     * 查询某店铺待审批的请求（approver1 为 storeId，approver1_status 待审）
     */
    @Select("SELECT * FROM requests WHERE approver1_id = #{storeId} AND approver1_status = '0' ORDER BY create_time DESC")
    List<Request> selectPendingByStore(@Param("storeId") Long storeId);

    /**
     * 查询所有待系统管理员审批的请求（approver2_status 待审）
     */
    @Select("SELECT * FROM requests WHERE approver2_status = '0' ORDER BY create_time DESC")
    List<Request> selectPendingSysAdmin();

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
