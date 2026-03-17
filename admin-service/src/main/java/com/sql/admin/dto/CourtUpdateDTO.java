package com.sql.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourtUpdateDTO {
    /**
     * 场地ID
     */
    @NotNull(message = "场地ID不能为空")
    private Long courtId;

    /**
     * 场地名称（可选）
     */
    private String courtName;

    /**
     * 状态：0-正常，1-维护中（可选）
     */
    private String status;
}
