package com.sql.common.entity;

import com.sql.common.entity.db.Admin;

import lombok.Data;

@Data
public class AdminOnline {
    /**
     * token
     * 随机UUID
     */
    private String token;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /*
     * 管理员信息
     */
    private Admin adminInfo;

}
