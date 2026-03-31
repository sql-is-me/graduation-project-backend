package com.sql.common.entity.bo;

import java.time.LocalDateTime;

import com.sql.common.entity.po.Admin;

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
    private LocalDateTime loginTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /*
     * 管理员信息
     */
    private Admin adminInfo;

}
