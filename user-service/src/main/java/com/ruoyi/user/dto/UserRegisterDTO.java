package com.ruoyi.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户/教练注册请求体
 */
@Data
public class UserRegisterDTO {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户类型（0:会员 1:教练）
     */
    @NotBlank(message = "用户类型不能为空")
    private String type;

    /**
     * 所属店铺ID（教练注册时必填）
     */
    private Long storeId;
}
