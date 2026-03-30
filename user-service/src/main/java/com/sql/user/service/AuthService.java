package com.sql.user.service;

import com.sql.user.dto.UserRegisterDTO;
import com.sql.user.dto.UserResetPasswordDTO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户/教练登录与注册服务
 */
public interface AuthService {
    /**
     * 用户/教练登录
     *
     * @param username 用户名
     * @param password 密码
     */
    String login(String username, String password);

    /**
     * 退出登录
     */
    void logout(HttpServletRequest request);

    /**
     * 用户/教练注册
     */
    void register(UserRegisterDTO dto);

    /**
     * 通过验证码重置密码
     */
    void resetPassword(UserResetPasswordDTO dto);

    /**
     * 发送邮箱验证码
     */
    String sendEmailCode(String email);
}
