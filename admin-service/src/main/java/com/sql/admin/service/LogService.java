package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.dto.LoginLogSelectDTO;
import com.sql.common.entity.dto.OperLogSelectDTO;
import com.sql.common.entity.po.LoginLog;
import com.sql.common.entity.po.OperLog;
import com.sql.common.entity.vo.LoginInfo;
import com.sql.common.entity.vo.OperLogInfo;

public interface LogService {
    /**
     * 查询操作日志列表（支持按操作人、标题、业务类型、操作状态筛选）
     */
    List<OperLogInfo> listOperLog(OperLogSelectDTO operLog);

    /**
     * 查询操作日志详情
     */
    OperLog getOperLog(Long operId);

    /**
     * 批量删除操作日志
     */
    void deleteOperLog(List<Long> operIds);

    /**
     * 清空操作日志
     */
    void cleanOperLog();

    /**
     * 新增操作日志（仅内部调用）
     */
    int insertOperLog(OperLog operLog);

    /**
     * 查询操作日志列表
     */
    List<LoginInfo> listLoginLog(LoginLogSelectDTO dto);

    /**
     * 查询登录日志详情
     */
    LoginLog getLoginLog(Long logId);

    /**
     * 批量删除登录日志
     */
    void deleteLoginLog(List<Long> LogIds);

    /**
     * 清空登录日志
     */
    void cleanLoginLog();

    /**
     * 新增登录日志（仅内部调用）
     */
    int insertLoginLog(LoginLog loginLog);
}
