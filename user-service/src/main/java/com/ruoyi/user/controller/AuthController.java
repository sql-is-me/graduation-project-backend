package com.ruoyi.user.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.entity.R;
import com.ruoyi.user.dto.UserLoginDTO;
import com.ruoyi.user.dto.UserRegisterDTO;
import com.ruoyi.user.service.AuthService;

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
        Map<String, Object> retMap = authService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return R.ok(retMap);
    }

    /**
     * 退出登录
     */
    @DeleteMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        authService.logout(request);
        return R.ok();
    }

    /**
     * 刷新Token有效期
     */
    @PostMapping("/refresh")
    public R<?> refresh(HttpServletRequest request) {
        authService.refreshToken(request);
        return R.ok();
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
