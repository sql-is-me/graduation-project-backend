package com.sql.admin.service;

import java.time.LocalDate;
import java.util.List;

import com.sql.common.entity.dto.CourseCreateDTO;
import com.sql.common.entity.vo.CourseDetailedInfo;
import com.sql.common.entity.vo.CourseInfo;

public interface CourseService {
    /**
     * 创建课程
     */
    long createCourse(CourseCreateDTO dto);

    /**
     * 取消课程
     */
    void cancelCourse(Long courseId);

    /**
     * 安排/更换教练
     */
    void assignCoach(Long courseId, Long coachId);

    /**
     * 批量安排孩子上课
     */
    int arrangeChildren(Long courseId, List<Long> childIds);

    /**
     * 取消孩子的上课安排
     */
    int cancelChild(Long courseId, Long childId);

    /**
     * 查询课程列表（按日期筛选）
     */
    List<CourseInfo> listCourses(Long StoreId, LocalDate courseDate);

    /**
     * 查询课程详情
     */
    CourseDetailedInfo getCourseById(Long courseId);
}
