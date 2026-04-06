package com.sql.common.entity.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sql.common.entity.po.Course;

import lombok.Data;

@Data
public class CourseInfo {
    private Long courseId;

    /**
     * 场地ID
     */
    private Long courtId;

    /**
     * 课程日期
     */
    private LocalDate courseDate;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 总课时数
     */
    private Integer totalHours;

    /**
     * 上课孩子数量
     */
    private Integer childNumber;

    /**
     * 上课教练名
     */
    private String coachName;

    /**
     * 课程状态
     * 0-准备中，1-进行中，2-已完成，3-已取消
     */
    private String status;

    /**
     * 核销状态
     * 0-未核销，1-已核销
     */
    private String verifyStatus;

    public CourseInfo(Course course, String coachName) {
        this.courseId = course.getCourseId();
        this.courtId = course.getCourtId();
        this.courseDate = course.getCourseDate();
        this.startTime = course.getStartTime();
        this.totalHours = course.getTotalHours();

        // 上课孩子数量
        List<Long> childIds = course.getChildIds();
        this.childNumber = childIds != null ? childIds.size() : 0;

        this.coachName = coachName;

        this.status = course.getStatus();
        this.verifyStatus = course.getVerifyStatus();
    }
}
