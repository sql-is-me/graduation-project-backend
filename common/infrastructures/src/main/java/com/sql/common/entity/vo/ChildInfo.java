package com.sql.common.entity.vo;

import com.sql.common.entity.po.Child;

import lombok.Data;

@Data
public class ChildInfo {
    /**
     * 孩子ID
     */
    private Long childId;

    /**
     * 家长ID（关联users表）
     */
    private Long parentId;

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

    /**
     * 孩子状态（0正常 1关闭）
     */
    private String status;

    public ChildInfo(Child child) {
        this.childId = child.getChildId();
        this.parentId = child.getParentId();
        this.childName = child.getChildName();
        this.photo = child.getPhoto();
        this.sex = child.getSex();
        this.status = child.getStatus();
    }
}
