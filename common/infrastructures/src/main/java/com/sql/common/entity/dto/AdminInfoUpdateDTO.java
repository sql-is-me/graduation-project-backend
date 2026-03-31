package com.sql.common.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员修改个人信息请求体
 */
@Data
public class AdminInfoUpdateDTO {
    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    private String nickName;

    /**
     * 手机号码
     */
    @NotBlank(message = "电话不能为空")
    private String phone;

    /**
     * 性别（0男 1女 2未知）
     */
    @NotBlank(message = "性别不能为空")
    private String sex;
}
