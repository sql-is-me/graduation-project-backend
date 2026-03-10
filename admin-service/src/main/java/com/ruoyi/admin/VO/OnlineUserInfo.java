package com.ruoyi.admin.VO;

import lombok.Data;

/**
 * 在线用户信息VO
 */
@Data
public class OnlineUserInfo {
    private String tokenId;

    private Long userId;

    private String userName;

    private String nickName;

    private String ipaddr;

    private Long loginTime;

    private String userType;
}
