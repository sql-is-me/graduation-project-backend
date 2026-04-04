package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.TeachingPlan;

import java.util.List;

@Mapper
public interface TeachingPlanMapper extends BaseMapper<TeachingPlan> {

    @Select("SELECT * FROM teaching_plans WHERE coach_id = #{coachId} ORDER BY create_time DESC")
    List<TeachingPlan> selectByCoachId(@Param("coachId") Long coachId);

    @Update("UPDATE teaching_plans SET status = #{status}, reject_reason = #{rejectReason}, update_time = now() WHERE tp_id = #{tpId}")
    int updateStatus(@Param("tpId") Long tpId, @Param("status") String status, @Param("rejectReason") String rejectReason);
}
