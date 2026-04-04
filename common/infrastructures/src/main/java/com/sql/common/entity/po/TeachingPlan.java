package com.sql.common.entity.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("teaching_plans")
public class TeachingPlan {
    @TableId(value = "tp_id", type = IdType.AUTO)
    private Long tpId;

    /** 教案标题 */
    private String title;

    /** 所属教练ID，关联users表 */
    @TableField("coach_id")
    private Long coachId;

    /** 所属店铺ID，关联stores表 */
    @TableField("store_id")
    private Long storeId;

    /** 教案文件相对路径 */
    @TableField("file_url")
    private String fileUrl;

    /** 教案简介 */
    private String description;

    /**
     * 审核状态
     * 0-待审核 1-审核通过 2-审核拒绝
     */
    private String status = "0";

    /** 拒绝原因 */
    @TableField("reject_reason")
    private String rejectReason;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
