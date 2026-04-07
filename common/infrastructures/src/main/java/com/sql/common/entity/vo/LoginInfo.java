package com.sql.common.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

import com.sql.common.entity.po.LoginLog;

@Data
public class LoginInfo {

    /**
     * 登录日志ID
     */
    private Long infoId;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 登录状态
     * 0=成功、1=失败
     */
    private String status;

    /**
     * 登录IP地址
     */
    private String ipAddr;

    /**
     * 访问时间
     */
    private LocalDateTime accessTime;

    public LoginInfo(LoginLog entity) {
        this.infoId = entity.getLogId();
        this.username = entity.getUsername();
        this.status = entity.getStatus();
        this.ipAddr = entity.getIpAddr();
        this.accessTime = entity.getAccessTime();
    }
}
