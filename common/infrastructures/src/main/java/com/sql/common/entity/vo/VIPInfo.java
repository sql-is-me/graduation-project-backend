package com.sql.common.entity.vo;

import java.util.List;

import com.sql.common.entity.po.Children;
import com.sql.common.entity.po.User;

import lombok.Data;

/**
 * 店铺会员详细信息视图对象
 * 包含会员基础信息、孩子信息及课时余额
 */
@Data
public class VIPInfo {
    /* 会员信息 */
    private Long userId;
    private String nickName;
    private String phone;
    private String email;
    private String sex;
    private String avatar;
    private String status;

    /** 课时余额 */
    private Integer remainingHours;

    /** 该会员的孩子列表 */
    private List<Children> children;

    public VIPInfo(User user, Integer remainingHours, List<Children> children) {
        this.userId = user.getUserId();
        this.nickName = user.getNickName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.sex = user.getSex();
        this.avatar = user.getAvatar();
        this.status = user.getStatus();
        this.remainingHours = remainingHours;
        this.children = children;
    }
}
