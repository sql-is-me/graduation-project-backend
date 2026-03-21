package com.sql.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员修改密码请求体
 */
@Data
public class AdminPasswordUpdateDTO {
    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
