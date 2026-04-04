package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import com.sql.common.entity.po.TeachingPlan;

import lombok.Data;

@Data
public class MyTeachingPlanInfo {
    private Long tpId;

    /** 教案标题 */
    private String title;

    /** 教案简介 */
    private String description;

    /**
     * 审核状态
     * 0-待审核 1-审核通过 2-审核拒绝
     */
    private String status = "0";

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

    public MyTeachingPlanInfo(TeachingPlan tp) {
        this.tpId = tp.getTpId();
        this.title = tp.getTitle();
        this.description = tp.getDescription();
        this.status = tp.getStatus();
        this.rejectReason = tp.getRejectReason();
        this.createTime = tp.getCreateTime();
        this.updateTime = tp.getUpdateTime();
    }
}
