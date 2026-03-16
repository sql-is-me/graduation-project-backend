package com.sql.common.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import java.util.Date;

/**
 * 孩子实体类
 * 对应数据库表 children
 */
@Data
@TableName("children")
public class Children {
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
    private Date birthday;

    /**
     * 孩子照片URL地址
     */
    private String photo = "";

    /**
     * 孩子性别（0男孩 1女孩 2未知）
     */
    private String sex = "0";

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private Date updateTime;
}
