package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterDTO {
    /**
     * 小程序登录凭证（用于获取openId）
     */
    @NotBlank(message = "登录凭证不能为空")
    private String code;

    /**
     * 用户类型：0-普通会员 1-教练
     */
    @NotBlank(message = "用户类型不能为空")
    private String userType;

    /**
     * 教练邀请码（userType为1时必填）
     */
    private String inviteCode;
}
