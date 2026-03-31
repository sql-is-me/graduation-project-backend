package com.sql.user.service;

import com.sql.user.dto.UserLoginDTO;

/**
 * 用户/教练登录与注册服务
 */
public interface AuthService {
    /**
     * 用户/教练登录
     *
     * @param code 小程序登录凭证
     */
    String login(UserLoginDTO dto);
}
