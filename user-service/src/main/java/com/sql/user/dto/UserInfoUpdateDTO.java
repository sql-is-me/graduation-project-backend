package com.sql.user.dto;

import lombok.Data;

/**
 * 用户/教练修改个人信息请求体
 */
@Data
public class UserInfoUpdateDTO {
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
    private String phone;

    /**
     * 性别（0男 1女 2未知）
     */
    private String sex;
}
