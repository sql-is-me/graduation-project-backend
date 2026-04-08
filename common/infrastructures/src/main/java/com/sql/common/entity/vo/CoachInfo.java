package com.sql.common.entity.vo;

import com.sql.common.entity.po.User;

import lombok.Data;

/**
 * 店铺教练详细信息视图对象
 */
@Data
public class CoachInfo {
    /* 教练详细信息 */
    private Long userId;
    private String nickName;
    private String phone;
    private String email;
    private String sex;
    private String avatar;
    private String photo;
    private String status;

    public CoachInfo(User user) {
        this.userId = user.getUserId();
        this.nickName = user.getNickName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.sex = user.getSex();
        this.avatar = user.getAvatar();
        this.photo = user.getPhoto();
        this.status = user.getStatus();
    }
}
