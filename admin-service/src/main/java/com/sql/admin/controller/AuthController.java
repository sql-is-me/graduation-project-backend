package com.sql.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.dto.AdminLoginDTO;
import com.sql.admin.dto.AdminRegisterDTO;
import com.sql.admin.dto.AdminResetPasswordDTO;
import com.sql.admin.service.AuthService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;

/**
 * 管理员认证控制器
 *
 * 登录接口需要验证码，验证码在网关层(ValidateCodeFilter)统一校验
 */
@RestController
@RequestMapping("/admin/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 管理员登录
     *
     * 验证码由网关层 ValidateCodeFilter 在请求到达此接口前校验完毕
     * 此接口只需处理用户名密码验证和Token生成
     */
    @PostMapping("/login")
    public R<?> login(@Valid @RequestBody AdminLoginDTO loginDTO) {
        String accessToken = authService.login(loginDTO.getUsername(), loginDTO.getPassword());

        return R.ok(accessToken, "登录成功");
    }

    /**
     * 管理员退出登录
     */
    @LoginRequired
    @DeleteMapping("/logout")
    public R<?> logout(HttpServletRequest request) {
        authService.logout(request);

        return R.ok("退出成功");
    }

    /**
     * 地区管理员注册（需邀请码）
     * 
     * 注册成为地区管理员需要另一位顶级管理员或地区管理员的邀请码
     * 顶级管理员无法通过注册创建，只能预置在数据库中
     */
    @PostMapping("/register")
    public R<?> register(@RequestBody AdminRegisterDTO registerBody) {
        authService.register(registerBody);
        return R.ok("注册成功");
    }

    /**
     * 生成邀请码
     *
     * 仅已登录的顶级管理员或地区管理员可调用
     * 顶级管理员生成时需指定storeId
     * 地区管理员生成时自动使用自身storeId
     */
    @Log(title = "生成邀请码", businessType = BusinessType.OTHER)
    @PostMapping("/invite")
    @LoginRequired
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    public R<?> generateInviteCode(@RequestParam(required = false) Long storeId,
            HttpServletRequest request) {
        String code = authService.generateInviteCode(request, storeId);

        return R.ok(code, "邀请码生成成功");
    }

    /**
     * 忘记密码 - 通过邮箱验证码重置密码
     */
    @Log(title = "忘记密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPassword")
    public R<?> resetPassword(@Valid @RequestBody AdminResetPasswordDTO dto) {
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
