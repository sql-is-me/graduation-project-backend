package com.ruoyi.admin.controller;

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

import com.ruoyi.admin.dto.AdminLoginDTO;
import com.ruoyi.admin.dto.AdminRegisterDTO;
import com.ruoyi.admin.service.AuthService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresLogin;
import com.ruoyi.common.security.annotation.RequiresRoles;

/**
 * 管理员认证控制器
 *
 * 登录接口需要验证码，验证码在网关层(ValidateCodeFilter)统一校验
 */
@RestController
@RequestMapping("admin")
public class AdminAuthController {
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
     *
     * 注册成为地区管理员需要另一位顶级管理员或地区管理员的邀请码
     * 顶级管理员无法通过注册创建，只能预置在数据库中
     */

    /**
     * 地区管理员注册（需邀请码）
     *
     * 此接口不需要登录即可访问，
     * 但必须持有有效的邀请码
     */
    @Log(title = "管理员注册", businessType = BusinessType.INSERT)
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
    @RequiresLogin
    @RequiresRoles(value = { "admin" }, logical = Logical.OR)
    @Log(title = "生成邀请码", businessType = BusinessType.OTHER)
    @PostMapping("/invite")
    public R<?> generateInviteCode(@RequestParam(required = false) Long storeId,
            HttpServletRequest request) {
        String code = authService.generateInviteCode(request, storeId);

        return R.ok(code, "邀请码生成成功");
    }

    // TODO: 访问权限管理
}
