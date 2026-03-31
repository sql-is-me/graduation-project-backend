package com.sql.common.entity.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("notices")
public class Notice {
    /**
     * 公告ID
     */
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告状态
     * 0正常 1关闭
     */
    private String status = "0"; // 默认正常

    /**
     * 创建者
     * 关联admins表
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新者
     * 关联admins表
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
