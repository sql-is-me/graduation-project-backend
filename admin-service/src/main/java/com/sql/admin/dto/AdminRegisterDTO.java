package com.sql.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员注册请求体（邀请码注册）
 */
@Data
public class AdminRegisterDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 邀请码
     */
    @NotBlank(message = "邀请码不能为空")
    private String inviteCode;

    /**
     * 门店ID（仅系统管理员推荐时需要传）
     */
    private Long storeId;
}
