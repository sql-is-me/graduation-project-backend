package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TrainingMethodsInfo {
    private Long tmId;

    /**
     * 训练方法标题
     */
    private String title;

    /**
     * 所属教练昵称
     */
    private String coachName;

    /**
     * 训练方法简介
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
