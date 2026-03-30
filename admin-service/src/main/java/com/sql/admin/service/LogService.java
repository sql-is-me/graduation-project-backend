package com.sql.admin.service;

import java.util.List;

import com.sql.admin.dto.LoginInfoSelectDTO;
import com.sql.admin.dto.OperLogSelectDTO;
import com.sql.common.entity.db.LoginInfo;
import com.sql.common.entity.db.OperLog;
import com.sql.common.vo.OperLogInfo;

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
    List<com.sql.common.vo.LoginInfo> listLoginInfo(LoginInfoSelectDTO dto);

    /**
     * 查询登录日志详情
     */
    LoginInfo getLoginInfo(Long infoId);

    /**
     * 批量删除登录日志
     */
    void deleteLoginInfo(List<Long> infoIds);

    /**
     * 清空登录日志
     */
    void cleanLoginInfo();

    /**
     * 新增登录日志（仅内部调用）
     */
    int insertLoginInfo(LoginInfo loginInfo);
}
