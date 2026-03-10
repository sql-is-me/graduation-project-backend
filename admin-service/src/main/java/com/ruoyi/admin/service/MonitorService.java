package com.ruoyi.admin.service;

import java.util.List;

import com.ruoyi.admin.VO.OnlineUserInfo;

public interface MonitorService {
    /**
     * 查询在线管理员列表
     */
    public List<OnlineUserInfo> getOnlineAdmins();

    /**
     * 查询在线用户列表
     */
    public List<OnlineUserInfo> getOnlineUsers();

    public void forceAdminLogout(String token);

    public void forceUserLogout(String token);
}
