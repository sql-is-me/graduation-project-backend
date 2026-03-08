package com.ruoyi.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.admin.dto.AdminRegisterDTO;
import com.ruoyi.admin.service.AdminLoginService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresLogin;
import com.ruoyi.common.security.annotation.RequiresRoles;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 管理员注册控制器（邀请码注册地区管理员）
 *
 * 注册成为地区管理员需要另一位顶级管理员或地区管理员的邀请码
 * 顶级管理员无法通过注册创建，只能预置在数据库中
 */
@RestController
@RequestMapping("/admin/register")
public class AdminRegisterController {

    @Autowired
    private AdminLoginService adminLoginService;

    @Autowired
    private TokenService tokenService;

    /**
     * 地区管理员注册（需邀请码）
     *
     * 此接口不需要登录即可访问（因为是注册），
     * 但必须持有有效的邀请码
     */
    @Log(title = "管理员注册", businessType = BusinessType.INSERT)
    @PostMapping
    public R<?> register(@RequestBody AdminRegisterDTO registerBody) {
        adminLoginService.registerAdmin(registerBody);
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
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @Log(title = "生成邀请码", businessType = BusinessType.OTHER)
    @PostMapping("/invite")
    public R<?> generateInviteCode(@RequestParam(required = false) Long storeId,
                                    HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (storeId == null) {
            storeId = loginUser.getSysUser().getDeptId();
        }
        String code = adminLoginService.generateAdminInviteCode(loginUser, storeId);
        return R.ok(code, "邀请码生成成功");
    }
}
