package com.ruoyi.admin.dto;

import lombok.Data;

/**
 * 管理员登录请求体
 */
@Data
public class LoginDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}
