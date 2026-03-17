package com.sql.admin.dto;

import lombok.Data;

@Data
public class CourtUpdateDTO {
    /**
     * 场地名称（可选）
     */
    private String courtName;

    /**
     * 状态：0-正常，1-维护中（可选）
     */
    private String status;
}
