package com.sql.common.entity.db;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("courts")
public class Court {
    @TableId(value = "court_id", type = IdType.AUTO)
    private Long courtId;

    /**
     * 场地名称
     */
    @TableField("court_name")
    private String courtName;

    /**
     * 所属店铺ID
     * 关联stores表
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 场地状态
     * 0-正常，1-维护中
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
