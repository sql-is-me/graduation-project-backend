package com.sql.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.entity.result.R;
import com.sql.user.dto.UserLoginDTO;
import com.sql.user.dto.UserRegisterDTO;
import com.sql.user.service.AuthService;

/**
 * 用户/教练认证控制器
 *
 * 登录接口需要验证码，验证码在网关层(ValidateCodeFilter)统一校验
 */
@RestController
@RequestMapping("/user/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 用户/教练登录
     *
     * 若用户在数据库中不存在，返回code=201，前端据此引导用户进入注册页面
     */
    @PostMapping("/login")
    public R<?> login(@Validated @RequestBody UserLoginDTO dto) {
        String accessToken = authService.login(dto);

        if (accessToken == null) {
            return R.fail(201, "用户未注册，请先完成注册");
        }

        return R.ok(accessToken, "登录成功");
    }

    /**
     * 用户注册
     *
     * 用户选择成为普通会员(userType=0)或教练(userType=1)
     * 选择教练时需提供店铺管理员生成的邀请码，注册后自动绑定对应店铺
     * 注册成功后自动完成登录，返回accessToken
     */
    @PostMapping("/register")
    public R<?> register(@Validated @RequestBody UserRegisterDTO dto) {
        String accessToken = authService.register(dto);

        return R.ok(accessToken, "注册成功");
    }
}
