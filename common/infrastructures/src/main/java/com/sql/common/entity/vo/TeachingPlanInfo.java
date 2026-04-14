package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import com.sql.common.entity.po.TeachingPlan;

import lombok.Data;

@Data
public class TeachingPlanInfo {
    private Long tpId;

    /** 教案标题 */
    private String title;

    /** 教案简介 */
    private String description;

    /** 所属教练ID，关联users表 */
    private Long coachId;

    /** 所属教练昵称 */
    private String coachName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public TeachingPlanInfo(TeachingPlan tp, String coachName) {
        this.tpId = tp.getTpId();
        this.title = tp.getTitle();
        this.description = tp.getDescription();
        this.coachId = tp.getCoachId();
        this.coachName = coachName;
        this.createTime = tp.getCreateTime();
        this.updateTime = tp.getUpdateTime();
    }
}
