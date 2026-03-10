package com.ruoyi.admin.service;

import java.util.List;

import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;

public interface LogService {
    /**
     * 查询操作日志列表（支持按操作人、标题、业务类型、操作状态筛选）
     */
    List<OperLog> listOperLog(OperLog operLog);

    /**
     * 查询操作日志详情
     */
    OperLog getOperLog(Long operId);

    /**
     * 删除操作日志
     */
    int deleteOperLog(Long operId);

    /**
     * 查询操作日志列表
     */
    List<LoginInfo> listLoginInfo(LoginInfo loginInfo);

    /**
     * 查询登录日志详情
     */
    LoginInfo getLoginInfo(Long infoId);

    /**
     * 删除登录日志
     */
    int deleteLoginInfo(Long infoId);
}
