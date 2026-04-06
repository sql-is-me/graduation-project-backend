package com.sql.admin.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.AttendanceMapper;
import com.sql.admin.mapper.ChildrenMapper;
import com.sql.admin.mapper.ClassHourMapper;
import com.sql.admin.mapper.CourseChildMapper;
import com.sql.admin.mapper.CourseMapper;
import com.sql.admin.service.AttendanceService;
import com.sql.common.entity.po.Attendance;
import com.sql.common.entity.po.Children;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.po.Course;
import com.sql.common.entity.po.AttendanceRecord;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseChildMapper courseChildMapper;

    @Autowired
    private ChildrenMapper childrenMapper;

    @Autowired
    private ClassHourMapper classHourMapper;

    @Override
    public Attendance getAttendance(Long courseId) {
        validateCourseOwnership(courseId);

        LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attendance::getCourseId, courseId);
        return attendanceMapper.selectOne(wrapper);
    }

    @Override
    public List<AttendanceRecord> listCourseChildren(Long courseId) {
        validateCourseOwnership(courseId);

        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceRecord::getCourseId, courseId)
                .orderByAsc(AttendanceRecord::getCreateTime);
        return courseChildMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void batchVerify(Long courseId, List<VerifyItem> items) {
        Long storeId = getStoreId();
        Course course = courseMapper.selectById(courseId);
        if (course == null || !course.getStoreId().equals(storeId)) {
            throw new ServiceException("课程不存在或无权操作");
        }

        // 校验签到签退照片已上传
        Attendance attendance = getAttendance(courseId);
        if (attendance == null || attendance.getSignInPhoto() == null) {
            throw new ServiceException("签到照片未上传，无法核销");
        }
        if (attendance.getSignOutPhoto() == null) {
            throw new ServiceException("签退照片未上传，无法核销");
        }

        Long adminId = ContextHolder.getAO().getAdminInfo().getAdminId();

        for (VerifyItem item : items) {
            LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttendanceRecord::getCourseId, courseId)
                    .eq(AttendanceRecord::getChildId, item.childId());
            AttendanceRecord cc = courseChildMapper.selectOne(wrapper);

            if (cc == null) {
                throw new ServiceException("孩子(ID:" + item.childId() + ")不在此课程中");
            }
            // 已请假的不可再核销
            if ("4".equals(cc.getStatus())) {
                continue;
            }
            // 已核销过的不重复处理
            if (!"0".equals(cc.getStatus())) {
                continue;
            }

            String status = item.status();
            if (!"1".equals(status) && !"2".equals(status) && !"3".equals(status)) {
                throw new ServiceException("无效的出勤状态：" + status + "，仅支持 1-正常完课 2-早退 3-缺勤");
            }

            // 早退或缺勤，返还课时
            if ("2".equals(status) || "3".equals(status)) {
                Children child = childrenMapper.selectById(item.childId());
                if (child != null) {
                    returnClassHours(child.getParentId(), course.getTotalHours());
                }
            }

            cc.setStatus(status);
            cc.setRemark(item.remark());
            cc.setVerifyAdminId(adminId);
            cc.setVerifyTime(LocalDateTime.now());
            courseChildMapper.updateById(cc);
        }
    }

    // ============ 私有方法 ============

    private Long getStoreId() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }
        return storeId;
    }

    private void validateCourseOwnership(Long courseId) {
        Long storeId = getStoreId();
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }
        if (!course.getStoreId().equals(storeId)) {
            throw new ServiceException("无权操作其他店铺的课程");
        }
    }

    private void returnClassHours(Long parentId, int hours) {
        LambdaQueryWrapper<ClassHour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassHour::getUserId, parentId);
        ClassHour classHour = classHourMapper.selectOne(wrapper);

        if (classHour != null) {
            classHour.setRemainingHours(classHour.getRemainingHours() + hours);
            classHour.setUsedHours(Math.max(0, classHour.getUsedHours() - hours));
            classHourMapper.updateById(classHour);
        }
    }
}
