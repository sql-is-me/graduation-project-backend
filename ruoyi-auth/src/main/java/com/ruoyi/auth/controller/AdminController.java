package com.ruoyi.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.auth.dto.AdminRegisterDTO;
import com.ruoyi.auth.dto.LoginDTO;
import com.ruoyi.auth.service.SysLoginService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.auth.AuthUtil;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 管理员登�?
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @PostMapping("login")
    public R<?> login(@RequestBody LoginDTO form) {
        LoginUser adminInfo = sysLoginService.login(form.getUsername(), form.getPassword());

        return R.ok(tokenService.createToken(adminInfo));
    }

    @DeleteMapping("logout")
    public R<?> logout(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);
            AuthUtil.logoutByToken(token);
            sysLoginService.logout(username);
        }
        return R.ok();
    }

    @PostMapping("refresh")
    public R<?> refresh(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            // 刷新令牌有效�?
            tokenService.refreshToken(loginUser);
            return R.ok();
        }
        return R.ok();
    }

    @PostMapping("register")
    public R<?> register(@RequestBody AdminRegisterDTO registerBody) {
        sysLoginService.registerAdmin(registerBody);
        return R.ok();
    }

    @PostMapping("invite")
    public R<?> invite(@RequestParam(required = false) Long storeId, HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (storeId == null) {
            storeId = loginUser.getSysUser().getDeptId();
        }
        String code = sysLoginService.generateAdminInviteCode(loginUser, storeId);
        return R.ok(code);
    }
}
