package com.sql.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.entity.result.R;
import com.sql.user.dto.UserLoginDTO;
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
     */
    @PostMapping("/login")
    public R<?> login(@Validated @RequestBody UserLoginDTO dto) {
        String accessToken = authService.login(dto);

        return R.ok(accessToken, "登录成功");
    }
}
