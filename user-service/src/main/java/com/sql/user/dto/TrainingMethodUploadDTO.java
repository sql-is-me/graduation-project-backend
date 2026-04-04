package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 训练方法上传请求体
 */
@Data
public class TrainingMethodUploadDTO {

    @NotBlank(message = "训练方法标题不能为空")
    private String title;

    private String description;
}
