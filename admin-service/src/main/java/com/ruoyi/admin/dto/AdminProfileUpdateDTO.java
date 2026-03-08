package com.ruoyi.admin.dto;

import lombok.Data;

/**
 * 管理员修改个人信息请求体
 */
@Data
public class AdminProfileUpdateDTO {
    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 性别（0男 1女 2未知）
     */
    private String sex;
}
