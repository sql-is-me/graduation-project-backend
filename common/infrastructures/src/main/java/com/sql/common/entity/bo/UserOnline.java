package com.sql.common.entity.bo;

import java.time.LocalDateTime;

import com.sql.common.entity.po.User;

import lombok.Data;

/**
 * 用户/教练在线信息缓存对象
 */
@Data
public class UserOnline {
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

    /**
     * 用户信息
     */
    private User userInfo;
}
