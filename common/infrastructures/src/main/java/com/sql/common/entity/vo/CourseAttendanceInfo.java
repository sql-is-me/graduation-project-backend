package com.sql.common.entity.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.sql.common.entity.po.Course;

import lombok.Data;

@Data
public class CourseAttendanceInfo {
    private Long courseId;

    /**
     * 上课孩子信息
     */
    private List<ChildAndAttendanceInfo> childAndAttendanceInfos;

    /**
     * 签到照片URL（绝对路径）
     */
    private String signInPhoto;

    /**
     * 签到时间
     */
    private LocalDateTime signInTime;

    /**
     * 签退照片URL（绝对路径）
     */
    private String signOutPhoto;

    /**
     * 签退时间
     */
    private LocalDateTime signOutTime;

    public CourseAttendanceInfo(Course course) {
        this.courseId = course.getCourseId();
        this.signInTime = course.getSignInTime();
        this.signOutTime = course.getSignOutTime();
    }
}
