package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.po.Attendance;
import com.sql.common.entity.po.AttendanceRecord;

/**
 * 考勤核销服务
 */
public interface AttendanceService {

    /**
     * 查询课程考勤记录（签到签退照片）
     */
    Attendance getAttendance(Long courseId);

    /**
     * 查询课程下所有孩子的出勤记录
     */
    List<AttendanceRecord> listCourseChildren(Long courseId);

    /**
     * 批量核销孩子出勤状态
     * 管理员对照签到签退照片，为每个孩子设置出勤状态：
     * 1-正常完课 2-早退 3-缺勤
     * 早退/缺勤会返还对应家长课时
     */
    void batchVerify(Long courseId, List<VerifyItem> items);

    /**
     * 核销条目
     */
    record VerifyItem(Long childId, String status, String remark) {}
}
