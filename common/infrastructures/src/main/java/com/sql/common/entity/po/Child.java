package com.sql.common.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 孩子实体类
 * 对应数据库表 children
 */
@Data
@TableName("children")
public class Child {
    /**
     * 孩子ID
     */
    @TableId(value = "child_id", type = IdType.AUTO)
    private Long childId;

    /**
     * 父母ID（关联用户表user_id）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 孩子姓名
     */
    @TableField("child_name")
    private String childName;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 孩子照片URL地址
     */
    private String photo = "/default_child_photo.jpg";

    /**
     * 孩子性别（0男孩 1女孩 2未知）
     */
    private String sex = "2";

    /**
     * 状态
     * 0正常 1关闭
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
}
