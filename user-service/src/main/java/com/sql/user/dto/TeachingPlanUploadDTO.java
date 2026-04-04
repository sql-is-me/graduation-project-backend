package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 教案上传请求体
 */
@Data
public class TeachingPlanUploadDTO {

    @NotBlank(message = "教案标题不能为空")
    private String title;

    private String description;
}
