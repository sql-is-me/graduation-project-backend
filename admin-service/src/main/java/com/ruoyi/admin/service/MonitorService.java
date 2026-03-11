package com.ruoyi.admin.service;

import java.util.List;

import com.ruoyi.common.VO.OnlineUserInfo;

public interface MonitorService {
    /**
     * 查询在线管理员列表
     */
    public List<OnlineUserInfo> getOnlineAdmins();

    /**
     * 查询在线用户列表
     */
    public List<OnlineUserInfo> getOnlineUsers();

    /**
     * 强制踢出地区管理员
     */
    public void forceAdminLogout(String token);

    /**
     * 强制踢出教练或会员
     */
    public void forceUserLogout(String token);
}
