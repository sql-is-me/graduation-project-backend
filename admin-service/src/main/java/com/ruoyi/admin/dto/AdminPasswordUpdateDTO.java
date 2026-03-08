package com.ruoyi.admin.dto;

import lombok.Data;

/**
 * 管理员修改密码请求体
 */
@Data
public class AdminPasswordUpdateDTO {
    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}
