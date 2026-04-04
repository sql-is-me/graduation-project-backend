package com.sql.user.service;

import com.sql.user.dto.UserLoginDTO;
import com.sql.user.dto.UserRegisterDTO;

/**
 * 用户/教练登录与注册服务
 */
public interface AuthService {
    /**
     * 用户/教练登录
     * 若用户不存在则返回null，由控制层引导前端进入注册流程
     *
     * @param dto 包含小程序登录凭证code
     */
    String login(UserLoginDTO dto);

    /**
     * 用户注册
     * 选择成为普通会员或教练，教练需提供邀请码
     *
     * @param dto 注册信息
     */
    String register(UserRegisterDTO dto);
}
