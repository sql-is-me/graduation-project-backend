package com.sql.user.dto;

import lombok.Data;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

/**
 * 新增孩子信息请求体
 */
@Data
public class ChildrenCreateDTO {

    /**
     * 孩子姓名
     */
    @NotBlank(message = "孩子姓名不能为空")
    private String childName;

    /**
     * 出生日期
     */
    private LocalDate birthday;

    /**
     * 孩子性别（0男孩 1女孩 2未知）
     */
    private String sex;
}
