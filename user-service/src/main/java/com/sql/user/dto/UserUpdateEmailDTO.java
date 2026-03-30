package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户/教练修改邮箱请求体
 */
@Data
public class UserUpdateEmailDTO {
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "邮箱验证码不能为空")
    private String emailCode;
}
