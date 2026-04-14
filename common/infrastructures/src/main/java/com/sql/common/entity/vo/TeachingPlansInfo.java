package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TeachingPlansInfo {
    private Long tpId;

    /**
     * 教案标题
     */
    private String title;

    /**
     * 所属教练昵称
     */
    private String coachName;

    /**
     * 教案简介
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
