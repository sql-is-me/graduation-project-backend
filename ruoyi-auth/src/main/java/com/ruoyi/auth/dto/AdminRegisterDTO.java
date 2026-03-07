package com.ruoyi.auth.dto;

import lombok.Data;

/**
 * 管理员注册对象
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
     * 推荐人ID
     */
    private Long referrerId;

    /**
     * 推荐码
     */
    private String inviteCode;

    /**
     * 门店ID（仅顶级管理员推荐时需要传）
     */
    private Long storeId;
}
