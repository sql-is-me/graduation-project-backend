package com.sql.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

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
     * 用户唯一标识
     */
    private String openid;

    /**
     * 用户在开放平台的唯一标识符
     */
    private String unionid;

    /**
     * 用户昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 用户类型
     * 0:会员 1:教练
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
    private String sex = "2";

    /**
     * 头像URL地址
     */
    private String avatar = "/default_user.jpg";

    /**
     * 所属店铺ID
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 账号状态（0正常 1停用）
     */
    private String status = "0";

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 是否是教练
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isCoach() {
        return Integer.parseInt(getUserType()) == 1;
    }
}
