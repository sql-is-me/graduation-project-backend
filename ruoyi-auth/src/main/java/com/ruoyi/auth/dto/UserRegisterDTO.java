package com.ruoyi.auth.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}