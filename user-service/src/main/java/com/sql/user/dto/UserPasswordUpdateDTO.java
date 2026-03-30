package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户/教练修改密码请求体
 */
@Data
public class UserPasswordUpdateDTO {
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
