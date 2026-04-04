package com.sql.admin.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.TrainingMethod;
import com.sql.common.entity.vo.TrainingMethodInfo;

@Mapper
public interface TrainingMethodMapper extends BaseMapper<TrainingMethod> {

    @Select("SELECT tm.tm_id, tm.title, u.nick_name AS coachNickName, tm.description, tm.status, tm.reject_reason AS rejectReason, tm.create_time AS createTime, tm.update_time AS updateTime " +
            "FROM training_methods tm LEFT JOIN users u ON tm.coach_id = u.user_id " +
            "WHERE tm.store_id = #{storeId} ORDER BY tm.create_time DESC")
    List<TrainingMethodInfo> selectByStoreId(@Param("storeId") Long storeId);

    @Select("SELECT * FROM training_methods WHERE tm_id = #{tmId}")
    TrainingMethod selectById(@Param("tmId") Long tmId);

    @Update("UPDATE training_methods SET status = #{status}, reject_reason = #{rejectReason}, update_time = now() WHERE tm_id = #{tmId}")
    int updateStatus(@Param("tmId") Long tmId, @Param("status") String status, @Param("rejectReason") String rejectReason);
}
