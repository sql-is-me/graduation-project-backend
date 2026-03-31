package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 在线用户信息VO
 */
@Data
public class OnlineUserInfo {
    private Long userId;

    private String userName;

    private String nickName;

    private String ipaddr;

    private LocalDateTime loginTime;

    private String userType;
}
