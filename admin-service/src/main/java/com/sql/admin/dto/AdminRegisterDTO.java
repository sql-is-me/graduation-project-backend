package com.sql.admin.dto;

import lombok.Data;

/**
 * 管理员注册请求体（邀请码注册）
 */
@Data
public class AdminRegisterDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 门店ID（仅顶级管理员推荐时需要传）
     */
    private Long storeId;
}
