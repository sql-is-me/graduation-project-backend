package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserResetPasswordDTO {
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
