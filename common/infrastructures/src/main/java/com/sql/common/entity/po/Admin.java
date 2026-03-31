package com.sql.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员实体类
 * 对应数据库表 admins
 */
@Data
@TableName("admins")
public class Admin {
    /**
     * 管理员ID
     */
    @TableId(value = "admin_id", type = IdType.AUTO)
    private Long adminId;

    /**
     * 管理员账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 管理员昵称
     */
    @TableField("nick_name")
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
    private String sex = "2"; // 默认未知

    /**
     * 头像URL地址
     */
    private String avatar = "/default_admin.jpg";

    /**
     * 所属店铺ID（管理员类型为STORE时必填）
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 管理员类型
     * 0:TOP 系统管理员 / 1:STORE 店铺管理员
     */
    @TableField("admin_type")
    private String adminType = "1"; // 默认店铺管理员

    /**
     * 推荐人ID（关联本表admin_id）
     */
    @TableField("referrer_id")
    private Long referrerId;

    /**
     * 账号状态
     * 0正常、1停用
     */
    private String status = "0"; // 默认正常

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

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isSysAdmin() {
        return Integer.parseInt(getAdminType()) == 0;
    }
}
