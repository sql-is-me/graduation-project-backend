package com.ruoyi.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.admin.dto.LoginDTO;
import com.ruoyi.admin.service.AuthService;
import com.ruoyi.common.core.domain.R;

/**
 * 管理员认证控制器
 *
 * 登录接口需要验证码，验证码在网关层(ValidateCodeFilter)统一校验
 */
@RestController
@RequestMapping("admin/auth")
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
    public R<?> login(@RequestBody LoginDTO loginDTO) {
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

    // TODO: try-catch
}
