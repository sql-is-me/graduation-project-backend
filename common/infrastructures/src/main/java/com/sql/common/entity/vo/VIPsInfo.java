package com.sql.common.entity.vo;

import com.sql.common.entity.po.User;

import lombok.Data;

/**
 * 店铺会员信息视图对象
 */
@Data
public class VIPsInfo {
    /* 会员信息 */
    private Long userId;
    private String nickName;
    private String phone;
    private String sex;
    private String status;

    public VIPsInfo(User user) {
        this.userId = user.getUserId();
        this.nickName = user.getNickName();
        this.phone = user.getPhone();
        this.sex = user.getSex();
        this.status = user.getStatus();
    }
}
