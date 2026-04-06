package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.admin.service.AttendanceService;
import com.sql.api.RemoteFileService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.dto.VerifyChildDTO;
import com.sql.common.entity.po.AttendanceRecord;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.file.FileUtils;

/**
 * 考勤管理（签到签退照片 + 批量核销孩子出勤状态）
 */
@RestController
@RequestMapping("/admin/attendance")
@LoginRequired
@RequiresType(UserTypes.MANAGER)
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 查询课程信息
     */
    @GetMapping("/course/{courseId}")
    public R<?> getAttendance(@PathVariable Long courseId) {
        Attendance attendance = attendanceService.getAttendance(courseId);
        if (attendance == null) {
            return R.ok("暂无考勤记录");
        }
        // 拼接完整照片 URL
        attendance.setSignInPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, attendance.getSignInPhoto()));
        attendance.setSignOutPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, attendance.getSignOutPhoto()));
        return R.ok(attendance);
    }

    /**
     * 查询课程下所有孩子的出勤记录
     */
    @GetMapping("/course/{courseId}/children")
    public R<?> listCourseChildren(@PathVariable Long courseId) {
        List<AttendanceRecord> list = attendanceService.listCourseChildren(courseId);
        return R.ok(list);
    }

    /**
     * 批量核销孩子出勤状态
     * 管理员对照签到签退照片，为每个孩子设置出勤状态
     * 请求体：[{"childId":1,"status":"1","remark":""},{"childId":2,"status":"3","remark":"缺勤"}]
     * status: 1-正常完课 2-早退出 3-缺勤
     */
    @Log(title = "考勤核销", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @PostMapping("/course/{courseId}/verify")
    public R<?> batchVerify(@PathVariable Long courseId,
            @RequestBody List<VerifyChildDTO> items) {
        List<AttendanceService.VerifyItem> verifyItems = items.stream()
                .map(dto -> new AttendanceService.VerifyItem(dto.getChildId(), dto.getStatus(), dto.getRemark()))
                .toList();
        attendanceService.batchVerify(courseId, verifyItems);
        return R.ok("核销完成");
    }
}
