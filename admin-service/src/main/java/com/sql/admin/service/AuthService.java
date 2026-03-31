package com.sql.admin.service;

import com.sql.common.entity.dto.AdminRegisterDTO;
import com.sql.common.entity.dto.AdminResetPasswordDTO;

import jakarta.servlet.http.HttpServletRequest;

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

    /**
     * 通过验证码重置密码
     */
    public void resetPassword(AdminResetPasswordDTO dto);

    /**
     * 发送邮箱验证码
     */
    public String sendEmailCode(String email);
}
