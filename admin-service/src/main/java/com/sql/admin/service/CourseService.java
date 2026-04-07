package com.sql.admin.service;

import java.time.LocalDate;
import java.util.List;

import com.sql.common.entity.dto.CourseCreateDTO;
import com.sql.common.entity.dto.VerifyChildDTO;
import com.sql.common.entity.vo.CourseAttendanceInfo;
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
    List<CourseInfo> listCourses(Long storeId, LocalDate courseDate);

    /**
     * 查询课程详情（管理员视角，含完整信息）
     */
    CourseDetailedInfo getCourseById(Long courseId);

    /**
     * 查询课程考勤信息（签到签退照片+时间，及每个孩子当前出勤状态）
     * 供管理员核销前查看
     */
    CourseAttendanceInfo getCourseAttendanceInfo(Long courseId);

    /**
     * 批量核销孩子出勤状态
     * 管理员对照签到签退照片，为每个孩子设置出勤状态：
     * 1-正常完课 2-迟到 3-早退 4-缺勤
     * 早退/缺勤返还对应家长课时；迟到不返还
     */
    void batchVerify(Long courseId, List<VerifyChildDTO> items);
}
