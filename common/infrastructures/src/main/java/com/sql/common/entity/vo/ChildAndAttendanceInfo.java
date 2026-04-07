package com.sql.common.entity.vo;

import lombok.Data;

@Data
public class ChildAndAttendanceInfo {
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

    /**
     * 课程状态
     * 0-准备中，1-进行中，2-已完成，3-已取消
     */
    private String status;
}
