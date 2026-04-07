package com.sql.common.entity.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sql.common.entity.po.Course;

import lombok.Data;

@Data
public class CourseDetailedInfo {
    private Long courseId;

    /**
     * 创建人ID
     * 关联admins表
     */
    private Long creatorId;

    /**
     * 所属店铺ID
     * 关联stores表
     */
    private Long storeId;

    /**
     * 场地ID
     * 关联场地表courts
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
     * 仅可取1-3
     */
    private Integer totalHours;

    /**
     * 上课孩子信息及出勤情况
     */
    private List<ChildAndAttendanceInfo> childAndAttendanceInfos;

    /**
     * 上课教练名称
     */
    private String coachName;

    /**
     * 上课教练头像
     */
    private String coachAvatar;

    /**
     * 课程状态
     * 0-准备中，1-进行中，2-已完成，3-已取消
     */
    private String status;

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

    /**
     * 核销状态
     * 0-未核销，1-已核销
     */
    private String verifyStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    public CourseDetailedInfo(Course course) {
        this.courseId = course.getCourseId();
        this.creatorId = course.getCreatorId();
        this.storeId = course.getStoreId();
        this.courtId = course.getCourtId();
        this.courseDate = course.getCourseDate();
        this.startTime = course.getStartTime();
        this.totalHours = course.getTotalHours();
        this.status = course.getStatus();
        this.signInTime = course.getSignInTime();
        this.signOutTime = course.getSignOutTime();
        this.verifyStatus = course.getVerifyStatus();
        this.createTime = course.getCreateTime();
        this.updateTime = course.getUpdateTime();
    }
}
