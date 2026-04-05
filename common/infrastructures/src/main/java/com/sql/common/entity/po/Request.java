package com.sql.common.entity.po;

import java.time.LocalDateTime;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import lombok.Data;

@Data
@TableName(value = "requests", autoResultMap = true)
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
     * 业务载荷（JSON），根据 type 不同存储不同字段：
     * VIP_LEAVE:                    {"courseId":1, "childId":2}
     * COACH_UPLOAD_TEACHING_PLAN:   {"tpId":1, "fileUrl":"/abs/path.pdf"}
     * COACH_UPLOAD_TRAINING_METHOD: {"tmId":1, "fileUrl":"/abs/path.pdf"}
     * VIP_BIND_STORE / COACH_BIND_STORE: {"targetStoreId":1}
     */
    @TableField(value = "payload", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> payload;

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

    /**
     * 审核人1 ID
     */
    @TableField("approver1_id")
    private Long approver1Id;

    /**
     * 审核人1状态
     * 0-待审 1-同意 2-拒绝
     */
    @TableField("approver1_status")
    private String approver1Status = "0";

    /**
     * 审核人2 ID
     */
    @TableField("approver2_id")
    private Long approver2Id;

    /**
     * 审核人2状态
     */
    @TableField("approver2_status")
    private String approver2Status;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
