package com.sql.common.entity.dto;

import lombok.Data;

@Data
public class NoticeUpdateDTO {
    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;
}
