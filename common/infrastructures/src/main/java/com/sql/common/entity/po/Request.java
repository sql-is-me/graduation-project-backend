package com.sql.common.entity.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("requests")
public class Request {
    @TableId(value = "request_id", type = IdType.AUTO)
    private Long requestId;

    /** 发起人ID */
    @TableField("sender_id")
    private Long senderId;

    /**
     * 发起人类型
     * 0-会员 1-教练
     */
    @TableField("sender_type")
    private String senderType;

    /**
     * 请求类型，见 RequestConstants
     */
    private String type;

    /**
     * 关联资源ID
     * 教案审核时为 tp_id，训练方法审核时为 tm_id，请假时为课程安排ID，换店时为 null
     */
    @TableField("ref_id")
    private Long refId;

    /**
     * 目标店铺ID（换店场景使用）
     */
    @TableField("target_store_id")
    private Long targetStoreId;

    /**
     * 整体状态
     * 0-待处理 1-全部同意 2-已拒绝
     */
    private String status = "0";

    /** 申请说明 */
    private String message;

    /** 拒绝原因 */
    @TableField("reject_reason")
    private String rejectReason;

    /** 审核人1 ID（店铺管理员 / 原店铺管理员） */
    @TableField("approver1_id")
    private Long approver1Id;

    /**
     * 审核人1状态
     * 0-待审 1-同意 2-拒绝
     */
    @TableField("approver1_status")
    private String approver1Status = "0";

    /** 审核人2 ID（目标店铺管理员，三方审核时使用） */
    @TableField("approver2_id")
    private Long approver2Id;

    /** 审核人2状态 */
    @TableField("approver2_status")
    private String approver2Status;

    /** 审核人3 ID（系统管理员，三方审核时使用，null表示任意系统管理员均可） */
    @TableField("approver3_id")
    private Long approver3Id;

    /** 审核人3状态 */
    @TableField("approver3_status")
    private String approver3Status;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
