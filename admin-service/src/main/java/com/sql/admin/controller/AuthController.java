package com.sql.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.dto.AdminLoginDTO;
import com.sql.admin.dto.AdminRegisterDTO;
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
        Map<String, Object> retMap = authService.login(loginDTO.getUsername(), loginDTO.getPassword());

        return R.ok(retMap);
    }

    /**
     * 管理员退出登录
     */
    // @LoginRequired
    // TODO: 测试完成后记得加入登录校验，确保只有已登录的管理员才能调用退出接口
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
     * 注册成为地区管理员需要另一位顶级管理员或地区管理员的邀请码
     * 顶级管理员无法通过注册创建，只能预置在数据库中
     */

    /**
     * 地区管理员注册（需邀请码）
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
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    public R<?> generateInviteCode(@RequestParam(required = false) Long storeId,
            HttpServletRequest request) {
        String code = authService.generateInviteCode(request, storeId);

        return R.ok(code, "邀请码生成成功");
    }
}
