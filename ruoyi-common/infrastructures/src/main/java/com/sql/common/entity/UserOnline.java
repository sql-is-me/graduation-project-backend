package com.sql.common.entity;

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
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 用户信息
     */
    private User userInfo;

    /**
     * 用户类型（0:会员 1:教练）
     */
    private String userType;
}
