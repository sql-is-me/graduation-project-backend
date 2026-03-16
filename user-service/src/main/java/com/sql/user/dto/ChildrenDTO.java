package com.sql.user.dto;

import lombok.Data;

import java.util.Date;

/**
 * 孩子信息请求体（新增/修改）
 */
@Data
public class ChildrenDTO {
    /**
     * 孩子ID（修改时需传入）
     */
    private Long childId;

    /**
     * 孩子姓名
     */
    private String childName;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 孩子照片URL地址
     */
    private String photo;

    /**
     * 孩子性别（0男孩 1女孩 2未知）
     */
    private String sex;
}
