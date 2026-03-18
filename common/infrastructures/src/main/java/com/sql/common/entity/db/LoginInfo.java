package com.sql.common.entity.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 登录日志记录实体类
 * 供顶级管理员查阅所有用户的登录/登出记录
 */
@Data
@TableName("loginInfo")
public class LoginInfo {

    /**
     * 日志主键
     */
    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

    /**
     * 用户账号
     */
    @TableField("username")
    private String username;

    /**
     * 登录状态
     * 0=成功、1=失败
     */
    private String status;

    /**
     * 登录IP地址
     */
    @TableField("ip_addr")
    private String ipAddr;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    @TableField("access_time")
    private LocalDateTime accessTime;
}
