package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 在线管理员信息VO
 */
@Data
public class OnlineAdminInfo {
    private Long userId;

    private String userName;

    private String nickName;

    private String ipaddr;

    private LocalDateTime loginTime;

    private String userType;
}
