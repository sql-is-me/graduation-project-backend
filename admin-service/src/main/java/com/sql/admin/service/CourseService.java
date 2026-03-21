package com.sql.admin.service;

import java.time.LocalDate;
import java.util.List;

import com.sql.admin.dto.CourseCreateDTO;
import com.sql.common.entity.db.Course;

public interface CourseService {
    /**
     * 创建课程
     */
    int createCourse(CourseCreateDTO dto);

    /**
     * 安排/更换教练
     */
    int assignCoach(Long courseId, Long coachId);

    /**
     * 取消课程
     */
    int cancelCourse(Long courseId);

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
    List<Course> listCourses(LocalDate courseDate);

    /**
     * 查询课程详情
     */
    Course getCourseById(Long courseId);
}
