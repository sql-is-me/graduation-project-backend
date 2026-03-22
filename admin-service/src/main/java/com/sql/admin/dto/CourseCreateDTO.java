package com.sql.admin.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseCreateDTO {
    /**
     * 场地ID
     */
    @NotNull(message = "场地ID不能为空")
    private Long courtId;

    /**
     * 课程日期
     */
    @NotNull(message = "课程日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseDate;

    /**
     * 开始时间（精确到小时）
     */
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    /**
     * 总课时数（1-3）
     */
    @NotNull(message = "课时数不能为空")
    @Min(value = 1, message = "课时数最少为1")
    @Max(value = 3, message = "课时数最多为3")
    private Integer totalHours;
}
