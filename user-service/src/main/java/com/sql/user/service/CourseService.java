package com.sql.user.service;

import java.time.LocalDate;
import java.util.List;

import com.sql.common.entity.vo.CourseDetailedInfo;
import com.sql.common.entity.vo.CourseInfo;

public interface CourseService {

    // ===== COACH =====

    /**
     * 教练上传课程签到照片（将课程状态变为进行中）
     */
    void uploadSignIn(Long courseId, String photoUrl);

    /**
     * 教练上传课程签退照片（将课程状态变为已完成）
     */
    void uploadSignOut(Long courseId, String photoUrl);

    /**
     * 查询教练自己的课程列表（可按日期筛选）
     */
    List<CourseInfo> listCoachCourses(LocalDate courseDate);

    /**
     * 查询课程详情（教练视角）
     * 含课程基本信息、所有孩子出勤情况及签到签退照片绝对URL
     * 仅教练本人可查看自己负责的课程
     */
    CourseDetailedInfo getCoachCourseDetail(Long courseId);

    // ===== VIP =====

    /**
     * 查询VIP自己孩子的课程列表（可按日期筛选）
     */
    List<CourseInfo> listVipCourses(LocalDate courseDate);

    /**
     * 查询课程详情（VIP视角）
     * 含签到签退照片绝对URL，孩子出勤信息仅限当前VIP名下的孩子
     */
    CourseDetailedInfo getVipCourseDetail(Long courseId);
}
