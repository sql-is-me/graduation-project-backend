package com.sql.common.entity.dto;

import lombok.Data;

@Data
public class LoginInfoSelectDTO {
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
}
