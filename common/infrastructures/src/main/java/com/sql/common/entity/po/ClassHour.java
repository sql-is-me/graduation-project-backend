package com.sql.common.entity.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("class_hours")
public class ClassHour {
    @TableId(value = "ch_id", type = IdType.AUTO)
    private Long chId;

    /**
     * 用户id（关联users表）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 总购买课时数
     */
    @TableField("hours")
    private Integer hours = 0;

    /**
     * 已用课时数
     */
    @TableField("used_hours")
    private Integer usedHours = 0;

    /**
     * 剩余课时数
     */
    @TableField("remaining_hours")
    private Integer remainingHours = 0;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
