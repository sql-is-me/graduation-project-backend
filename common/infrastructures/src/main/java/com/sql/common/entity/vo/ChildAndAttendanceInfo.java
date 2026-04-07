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
     * 出勤状态
     * 0-待出勤 1-正常完课 2-迟到 3-早退 4-缺勤 5-请假
     */
    private String status;
}
