package com.sql.common.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员更换邮箱
 */
@Data
public class AdminUpdateEmailDTO {

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String emailCode;
}
