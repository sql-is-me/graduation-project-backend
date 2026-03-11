package com.ruoyi.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import java.util.Date;

/**
 * 用户/教练统一实体类
 * type=0 表示普通会员
 * type=1 表示教练
 * 统一映射 users 表
 */
@Data
@TableName("users")
public class User {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 用户类型（0:会员 1:教练）
     */
    @TableField("user_type")
    private String userType;

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

    /**
     * 头像URL地址
     */
    private String avatar;

    /**
     * 所属店铺ID（仅教练有此字段）
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 账号状态（0正常 1停用）
     */
    private String status;

    /**
     * 最后登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @TableField("login_date")
    private Date loginDate;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否是教练
     */
    public boolean isCoach() {
        return "1".equals(userType);
    }
}
