package com.sql.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.TrainingMethod;
import com.sql.common.entity.vo.TrainingMethodsInfo;

import java.util.List;

@Mapper
public interface TrainingMethodMapper extends BaseMapper<TrainingMethod> {

    @Select("SELECT * FROM training_methods WHERE coach_id = #{coachId} ORDER BY create_time DESC")
    List<TrainingMethod> selectByCoachId(@Param("coachId") Long coachId);

    @Select("SELECT tm.tm_id, tm.title, u.nick_name AS coachName, tm.description, tm.status, tm.reject_reason AS rejectReason, tm.create_time AS createTime, tm.update_time AS updateTime " +
            "FROM training_methods tm LEFT JOIN users u ON tm.coach_id = u.user_id " +
            "WHERE tm.store_id = #{storeId} AND tm.status = '1' ORDER BY tm.create_time DESC")
    List<TrainingMethodsInfo> selectApprovedByStoreId(@Param("storeId") Long storeId);

    @Update("UPDATE training_methods SET status = #{status}, reject_reason = #{rejectReason}, update_time = now() WHERE tm_id = #{tmId}")
    int updateStatus(@Param("tmId") Long tmId, @Param("status") String status, @Param("rejectReason") String rejectReason);
}
