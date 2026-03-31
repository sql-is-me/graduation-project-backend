package com.sql.common.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourtCreateDTO {
    /**
     * 场地名称
     */
    @NotBlank(message = "场地名称不能为空")
    private String courtName;
}
