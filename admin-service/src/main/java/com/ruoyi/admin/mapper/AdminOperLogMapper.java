package com.ruoyi.admin.mapper;

import java.util.List;
import com.ruoyi.system.api.domain.SysOperLog;

/**
 * 操作日志Mapper接口
 */
public interface AdminOperLogMapper {

    /**
     * 查询操作日志列表
     *
     * @param operLog 操作日志查询条件
     * @return 操作日志集合
     */
    public List<SysOperLog> selectOperLogList(SysOperLog operLog);

    /**
     * 根据ID查询操作日志
     *
     * @param operId 操作ID
     * @return 操作日志
     */
    public SysOperLog selectOperLogById(Long operId);
}
