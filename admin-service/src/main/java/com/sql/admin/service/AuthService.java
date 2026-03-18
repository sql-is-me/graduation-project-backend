package com.sql.admin.service;

import com.sql.admin.dto.AdminRegisterDTO;

import jakarta.servlet.http.HttpServletRequest;;

/**
 * 管理员登录与注册服务
 */
public interface AuthService {
    /**
     * 管理员登录
     */
    public String login(String username, String password);

    /**
     * 退出登录
     */
    public void logout(HttpServletRequest request);

    /**
     * 管理员注册
     */
    public void register(AdminRegisterDTO dto);

    /**
     * 生成管理员注册邀请码
     */
    public String generateInviteCode(HttpServletRequest request, Long storeId);
}
