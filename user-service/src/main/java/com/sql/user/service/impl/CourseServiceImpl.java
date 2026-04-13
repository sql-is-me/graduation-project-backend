package com.sql.user.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.common.entity.po.AttendanceRecord;
import com.sql.common.entity.po.Child;
import com.sql.common.entity.po.Course;
import com.sql.common.entity.po.User;
import com.sql.common.entity.vo.ChildAndAttendanceInfo;
import com.sql.common.entity.vo.CourseDetailedInfo;
import com.sql.common.entity.vo.CourseInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.utils.file.FileUtils;
import com.sql.user.mapper.ChildMapper;
import com.sql.user.mapper.CourseChildMapper;
import com.sql.user.mapper.CourseMapper;
import com.sql.user.mapper.UserMapper;
import com.sql.user.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseChildMapper courseChildMapper;

    @Autowired
    private ChildMapper childMapper;

    @Autowired
    private UserMapper userMapper;

    // ===== COACH =====

    @Override
    @Transactional
    public void uploadSignIn(Long courseId, String photoUrl) {
        Long coachId = getCoachId();
        Course course = getCourseAndValidateCoach(courseId, coachId);

        if (!"0".equals(course.getStatus())) {
            throw new ServiceException("课程不处于准备状态，无法上传签到照片");
        }
        if (course.getSignInPhoto() != null) {
            throw new ServiceException("签到照片已上传，无法重复操作");
        }

        course.setSignInPhoto(photoUrl);
        course.setSignInTime(LocalDateTime.now());
        course.setStatus("1"); // 进行中
        courseMapper.updateById(course);
    }

    @Override
    @Transactional
    public void uploadSignOut(Long courseId, String photoUrl) {
        Long coachId = getCoachId();
        Course course = getCourseAndValidateCoach(courseId, coachId);

        if (!"1".equals(course.getStatus())) {
            throw new ServiceException("课程未处于进行中状态，无法上传签退照片");
        }
        if (course.getSignOutPhoto() != null) {
            throw new ServiceException("签退照片已上传，无法重复操作");
        }

        course.setSignOutPhoto(photoUrl);
        course.setSignOutTime(LocalDateTime.now());
        course.setStatus("2"); // 已完成
        courseMapper.updateById(course);
    }

    @Override
    public List<CourseInfo> listCoachCourses(LocalDate courseDate) {
        Long coachId = getCoachId();

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCoachId, coachId);
        if (courseDate != null) {
            wrapper.eq(Course::getCourseDate, courseDate);
        }
        wrapper.orderByAsc(Course::getCourseDate).orderByAsc(Course::getStartTime);

        List<Course> courses = courseMapper.selectList(wrapper);
        User coach = userMapper.selectById(coachId);
        String coachName = coach != null ? coach.getNickName() : null;

        List<CourseInfo> result = new ArrayList<>();
        for (Course course : courses) {
            result.add(new CourseInfo(course, coachName));
        }
        return result;
    }

    @Override
    public CourseDetailedInfo getCoachCourseDetail(Long courseId) {
        Long coachId = getCoachId();
        Course course = getCourseAndValidateCoach(courseId, coachId);

        // 教练信息
        User coach = userMapper.selectById(coachId);
        String coachName = coach != null ? coach.getNickName() : null;
        String coachAvatar = coach != null
                ? FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, coach.getAvatar())
                : null;

        // 所有孩子出勤信息
        List<ChildAndAttendanceInfo> caInfos = buildChildAttendanceInfos(courseId, course.getChildIds(), null);

        CourseDetailedInfo info = new CourseDetailedInfo(course);
        info.setCoachName(coachName);
        info.setCoachAvatar(coachAvatar);
        info.setSignInPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, course.getSignInPhoto()));
        info.setSignOutPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, course.getSignOutPhoto()));
        info.setChildAndAttendanceInfos(caInfos);
        return info;
    }

    // ===== VIP =====

    @Override
    public List<CourseInfo> listVipCourses(LocalDate courseDate) {
        Long parentId = ContextHolder.getUO().getUserInfo().getUserId();

        List<Child> children = childMapper.selectByParentId(parentId);
        if (children == null || children.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> myChildIds = children.stream().map(Child::getChildId).toList();

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (courseDate != null) {
            wrapper.eq(Course::getCourseDate, courseDate);
        }
        wrapper.orderByAsc(Course::getCourseDate).orderByAsc(Course::getStartTime);

        List<CourseInfo> result = new ArrayList<>();
        for (Course course : courseMapper.selectList(wrapper)) {
            if (course.getChildIds() == null)
                continue;
            if (course.getChildIds().stream().noneMatch(myChildIds::contains))
                continue;

            String coachName = null;
            if (course.getCoachId() != null) {
                User coach = userMapper.selectById(course.getCoachId());
                coachName = coach != null ? coach.getNickName() : null;
            }
            result.add(new CourseInfo(course, coachName));
        }
        return result;
    }

    @Override
    public CourseDetailedInfo getVipCourseDetail(Long courseId) {
        Long parentId = ContextHolder.getUO().getUserInfo().getUserId();

        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }

        // 获取自己名下的孩子ID集合
        List<Child> myChildren = childMapper.selectByParentId(parentId);
        Set<Long> myChildIds = myChildren.stream().map(Child::getChildId).collect(Collectors.toSet());

        // 校验该课程下至少有一个属于自己的孩子
        boolean hasChild = course.getChildIds() != null &&
                course.getChildIds().stream().anyMatch(myChildIds::contains);
        if (!hasChild) {
            throw new ServiceException("无权查看该课程详情");
        }

        // 只返回属于自己的孩子的出勤信息
        List<Long> filteredChildIds = course.getChildIds().stream()
                .filter(myChildIds::contains)
                .toList();

        List<ChildAndAttendanceInfo> caInfos = buildChildAttendanceInfos(courseId, filteredChildIds, null);

        String coachName = null;
        String coachAvatar = null;
        if (course.getCoachId() != null) {
            User coach = userMapper.selectById(course.getCoachId());
            coachName = coach != null ? coach.getNickName() : null;
            coachAvatar = coach != null ? FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, coach.getAvatar()) : null;
        }
        CourseDetailedInfo info = new CourseDetailedInfo(course);
        info.setSignInPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, course.getSignInPhoto()));
        info.setSignOutPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, course.getSignOutPhoto()));
        info.setChildAndAttendanceInfos(caInfos);
        info.setCoachName(coachName);
        info.setCoachAvatar(coachAvatar);

        return info;
    }

    // ===== 私有工具方法 =====

    private Long getCoachId() {
        User user = ContextHolder.getUO().getUserInfo();
        if (user == null || !user.isCoach()) {
            throw new ServiceException("当前用户不是教练");
        }
        return user.getUserId();
    }

    private Course getCourseAndValidateCoach(Long courseId, Long coachId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }
        if (!coachId.equals(course.getCoachId())) {
            throw new ServiceException("无权操作该课程");
        }
        return course;
    }

    /**
     * 构建孩子出勤信息列表
     *
     * @param courseId  课程ID
     * @param childIds  需要构建的孩子ID列表
     * @param filterIds 若不为null，只包含此集合内的孩子（用于VIP过滤），传null则包含全部
     */
    private List<ChildAndAttendanceInfo> buildChildAttendanceInfos(Long courseId, List<Long> childIds,
            Set<Long> filterIds) {
        if (childIds == null || childIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<AttendanceRecord> records = courseChildMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecord>()
                        .eq(AttendanceRecord::getCourseId, courseId));

        List<ChildAndAttendanceInfo> caInfos = new ArrayList<>();
        for (Long childId : childIds) {
            if (filterIds != null && !filterIds.contains(childId))
                continue;

            Child child = childMapper.selectById(childId);
            if (child == null)
                continue;

            ChildAndAttendanceInfo caInfo = new ChildAndAttendanceInfo();
            caInfo.setChildId(childId);
            caInfo.setChildName(child.getChildName());
            caInfo.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_CHILD_PHOTO, child.getPhoto()));
            caInfo.setSex(child.getSex());

            records.stream()
                    .filter(r -> r.getChildId().equals(childId))
                    .findFirst()
                    .ifPresent(r -> caInfo.setStatus(r.getStatus()));

            caInfos.add(caInfo);
        }
        return caInfos;
    }
}
