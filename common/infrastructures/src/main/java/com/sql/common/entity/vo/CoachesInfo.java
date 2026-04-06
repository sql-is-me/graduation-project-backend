package com.sql.common.entity.vo;

import com.sql.common.entity.po.User;

import lombok.Data;

/**
 * 店铺教练信息视图对象
 */
@Data
public class CoachesInfo {
    /* 教练信息 */
    private String nickName;
    private String phone;
    private String sex;
    private String status;

    public CoachesInfo(User user) {
        this.nickName = user.getNickName();
        this.phone = user.getPhone();
        this.sex = user.getSex();
        this.status = user.getStatus();
    }
}
