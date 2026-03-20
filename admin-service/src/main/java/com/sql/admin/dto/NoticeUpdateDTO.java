package com.sql.admin.dto;

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

    /**
     * 公告状态
     * 0正常 1关闭
     */
    private String status;
}
