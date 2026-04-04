package com.sql.admin.service;

import com.sql.common.entity.vo.TableDataInfo;

public interface MonitorService {
    /**
     * 查询在线管理员列表（分页+排序）
     */
    public TableDataInfo getOnlineAdmins(int pageNum, int pageSize, String orderByColumn, boolean asc);

    /**
     * 查询在线用户列表（分页+排序）
     */
    public TableDataInfo getOnlineUsers(int pageNum, int pageSize, String orderByColumn, boolean asc);

    /**
     * 强制踢出店铺管理员
     */
    public void forceAdminLogout(String adminId);

    /**
     * 强制踢出教练或会员
     */
    public void forceUserLogout(String token);

    /**
     * 封禁用户（会员或教练）
     */
    public void banUser(Long userId);

    /**
     * 解禁用户（会员或教练）
     */
    public void unbanUser(Long userId);
}
