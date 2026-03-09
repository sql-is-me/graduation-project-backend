package com.ruoyi.admin.service;

import com.ruoyi.common.entity.Admin;;

/**
 * 管理员登录与注册服务
 */
public interface AuthService {
    /**
     * 管理员登录
     */
    public Admin login(String username, String password);

    /**
     * 退出登录
     */
    public void logout(String loginName);
}
