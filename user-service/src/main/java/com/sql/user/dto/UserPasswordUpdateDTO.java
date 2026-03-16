package com.sql.user.dto;

import lombok.Data;

/**
 * 用户/教练修改密码请求体
 */
@Data
public class UserPasswordUpdateDTO {
    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
