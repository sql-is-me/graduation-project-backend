package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求体
 */
@Data
public class UserLoginDTO {
    /**
     * 小程序登录凭证
     */
    @NotBlank(message = "登录凭证不能为空")
    private String code;
}
