package com.sql.common.vo;

import com.sql.common.entity.db.Admin;

import lombok.Data;

@Data
public class AdminInfo {
    /**
     * 管理员账号
     */
    private String username;

    /**
     * 管理员昵称
     */
    private String nickName;

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
    private String sex; // 默认未知

    /**
     * 头像URL地址
     */
    private String avatar;

    /**
     * 所属店铺ID（管理员类型为STORE时必填）
     */
    private Long storeId;

    /**
     * 管理员类型
     * 0:TOP 超级管理员 / 1:STORE 店铺管理员
     */
    private String adminType; // 默认店铺管理员

    public AdminInfo(Admin admin) {
        this.username = admin.getUsername();
        this.nickName = admin.getNickName();
        this.email = admin.getEmail();
        this.phone = admin.getPhone();
        this.sex = admin.getSex();
        this.avatar = admin.getAvatar();
        this.storeId = admin.getStoreId();
        this.adminType = admin.getAdminType();
    }
}