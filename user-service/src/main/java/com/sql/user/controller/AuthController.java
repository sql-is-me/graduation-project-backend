package com.sql.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.entity.result.R;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.dto.UserLoginDTO;
import com.sql.user.dto.UserRegisterDTO;
import com.sql.user.dto.UserResetPasswordDTO;
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
     * 验证码由网关层 ValidateCodeFilter 在请求到达此接口前校验完毕
     * 此接口只需处理用户名密码验证和Token生成
     */
    @PostMapping("/login")
    public R<?> login(@Validated @RequestBody UserLoginDTO loginDTO) {
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
    public R<?> register(@Validated @RequestBody UserRegisterDTO registerBody) {
        authService.register(registerBody);
        return R.ok("注册成功");
    }

    /**
     * 忘记密码 - 通过邮箱验证码重置密码
     */
    @Log(title = "忘记密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPassword")
    public R<?> resetPassword(@Validated @RequestBody UserResetPasswordDTO dto) {
        authService.resetPassword(dto);
        return R.ok("密码重置成功，请重新登录");
    }

    /**
     * 发送邮箱验证码
     */
    @Log(title = "邮箱验证码", businessType = BusinessType.UPDATE)
    @GetMapping("/emailCode")
    public R<?> sendEmailCode(@RequestParam String email) {
        String code = authService.sendEmailCode(email);
        return R.ok(code, "验证码已发送");
    }
}
