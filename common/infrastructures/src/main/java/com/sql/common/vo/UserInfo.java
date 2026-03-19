package com.sql.common.vo;

import com.sql.common.entity.db.User;

import lombok.Data;

@Data
public class UserInfo {
    /**
     * 用户账号
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（0:会员 1:教练）
     */
    private String userType;

    /**
     * 邮箱
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

    /**
     * 头像URL地址
     */
    private String avatar;

    /**
     * 所属店铺ID
     */
    private Long storeId;

    public UserInfo(User user) {
        this.username = user.getUsername();
        this.nickName = user.getNickName();
        this.userType = user.getUserType();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.sex = user.getSex();
        this.avatar = user.getAvatar();
        this.storeId = user.getStoreId();
    }
}
