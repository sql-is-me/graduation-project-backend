package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import com.sql.common.entity.po.TrainingMethod;

import lombok.Data;

@Data
public class TrainingMethodInfo {
    private Long tmId;

    /**
     * 训练方法标题
     */
    private String title;

    /**
     * 训练方法简介
     */
    private String description;

    /** 所属教练ID，关联users表 */
    private Long coachId;

    /** 所属教练昵称 */
    private String coachName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public TrainingMethodInfo(TrainingMethod tm, String coachName) {
        this.tmId = tm.getTmId();
        this.title = tm.getTitle();
        this.description = tm.getDescription();
        this.coachId = tm.getCoachId();
        this.coachName = coachName;
        this.createTime = tm.getCreateTime();
        this.updateTime = tm.getUpdateTime();
    }
}
