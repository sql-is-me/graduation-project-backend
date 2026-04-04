package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TrainingMethodInfo {
    private Long tmId;

    /** 训练方法标题 */
    private String title;

    /** 所属教练昵称 */
    private String coachName;

    /** 训练方法简介 */
    private String description;

    /**
     * 审核状态
     * 0-待审核 1-审核通过 2-审核拒绝
     */
    private String status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
