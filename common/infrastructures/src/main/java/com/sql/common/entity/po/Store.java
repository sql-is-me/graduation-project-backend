package com.sql.common.entity.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("stores")
public class Store {
    @TableId(value = "store_id", type = IdType.AUTO)
    private Long storeId;

    /**
     * 创建人ID
     * 关联admins表
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 店铺名称
     */
    @TableField("store_name")
    private String storeName;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 店铺所有者ID
     * 关联admins表
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 店铺状态
     * 0-正常，1-停业
     */
    private String status = "0"; // 默认正常

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
