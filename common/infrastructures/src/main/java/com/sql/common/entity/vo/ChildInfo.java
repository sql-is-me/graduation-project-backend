package com.sql.common.entity.vo;

import lombok.Data;

@Data
public class ChildInfo {
    /**
     * 孩子ID
     */
    private Long childId;

    /**
     * 孩子姓名
     */
    private String childName;

    /**
     * 孩子照片URL地址
     */
    private String photo;

    /**
     * 孩子性别（0男孩 1女孩 2未知）
     */
    private String sex;
}
