package com.sql.common.entity.vo;

import com.sql.common.entity.po.User;

import lombok.Data;

/**
 * 店铺教练信息视图对象
 */
@Data
public class CoachesInfo {
    /* 教练信息 */
    private Long coachId;
    private String nickName;
    private String phone;
    private String sex;
    private String photo;
    private String status;

    public CoachesInfo(User user) {
        this.coachId = user.getUserId();
        this.nickName = user.getNickName();
        this.phone = user.getPhone();
        this.sex = user.getSex();
        this.photo = user.getPhoto();
        this.status = user.getStatus();
    }
}
