package com.sql.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.TeachingPlan;
import com.sql.common.entity.vo.TeachingPlansInfo;

@Mapper
public interface TeachingPlanMapper extends BaseMapper<TeachingPlan> {

    @Select("SELECT tp.tp_id, tp.title, u.nick_name AS coachName, tp.description, tp.create_time AS createTime " +
            "FROM teaching_plans tp LEFT JOIN users u ON tp.coach_id = u.user_id " +
            "WHERE tp.store_id = #{storeId} ORDER BY tp.create_time DESC")
    List<TeachingPlansInfo> selectByStoreId(@Param("storeId") Long storeId);

    @Update("UPDATE teaching_plans SET status = #{status}, reject_reason = #{rejectReason}, update_time = now() WHERE tp_id = #{tpId}")
    int updateStatus(@Param("tpId") Long tpId, @Param("status") String status, @Param("rejectReason") String rejectReason);
}
