package com.sql.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveRequestDTO {

    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @NotNull(message = "孩子ID不能为空")
    private Long childId;

    private String message;
}
