package com.sql.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.entity.result.R;
import com.sql.user.dto.UserLoginDTO;
import com.sql.user.dto.UserRegisterDTO;
import com.sql.user.service.AuthService;

/**
 * 用户/教练认证控制器
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
    public R<?> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        String accessToken = authService.login(loginDTO.getUsername(), loginDTO.getPassword());

        return R.ok(accessToken, "登录成功");
    }

    /**
     * 退出登录
     */
    @LoginRequired
    @DeleteMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        authService.logout(request);

        return R.ok("退出成功");
    }

    /**
     * 用户/教练注册
     */
    @PostMapping("/register")
    public R<?> register(@Valid @RequestBody UserRegisterDTO registerBody) {
        authService.register(registerBody);
        return R.ok("注册成功");
    }
}
