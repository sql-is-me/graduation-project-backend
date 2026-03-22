package com.sql.admin.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.dto.CourseCreateDTO;
import com.sql.admin.mapper.ChildrenMapper;
import com.sql.admin.mapper.CourtMapper;
import com.sql.admin.mapper.CourseMapper;
import com.sql.admin.mapper.UserMapper;
import com.sql.admin.service.CourseService;
import com.sql.common.entity.db.Children;
import com.sql.common.entity.db.Court;
import com.sql.common.entity.db.Course;
import com.sql.common.entity.db.User;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;

@Service
public class CourseServiceImpl implements CourseService {

    /** 营业开始时间 9:00 */
    private static final LocalTime BUSINESS_START = LocalTime.of(9, 0);
    /** 营业结束时间 20:00 */
    private static final LocalTime BUSINESS_END = LocalTime.of(20, 0);

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourtMapper courtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChildrenMapper childrenMapper;

    @Override
    @Transactional
    public long createCourse(CourseCreateDTO dto) {
        Long storeId = getStoreId();
        Long adminId = ContextHolder.getAO().getAdminInfo().getAdminId();

        // 校验场地是否属于当前店铺
        Court court = courtMapper.selectById(dto.getCourtId());
        if (court == null || !court.getStoreId().equals(storeId)) {
            throw new ServiceException("场地不存在或不属于当前店铺");
        }
        if ("1".equals(court.getStatus())) {
            throw new ServiceException("该场地维护中，无法创建课程");
        }

        // 校验时间合法性
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime = startTime.plusHours(dto.getTotalHours());

        if (startTime.isBefore(BUSINESS_START)) {
            throw new ServiceException("课程开始时间不能早于营业时间9:00");
        }
        if (endTime.isAfter(BUSINESS_END)) {
            throw new ServiceException("课程结束时间不能晚于营业时间20:00");
        }

        // 校验课程日期不能是过去
        if (dto.getCourseDate().isBefore(LocalDate.now())) {
            throw new ServiceException("课程日期不能早于今天");
        }

        // 校验同一场地同一时间段是否有冲突
        checkTimeConflict(dto.getCourtId(), dto.getCourseDate(), startTime, endTime, null);

        Course course = new Course();
        course.setCreatorId(adminId);
        course.setStoreId(storeId);
        course.setCourtId(dto.getCourtId());
        course.setCourseDate(dto.getCourseDate());
        course.setStartTime(LocalDateTime.of(dto.getCourseDate(), dto.getStartTime()));
        course.setTotalHours(dto.getTotalHours());
        course.setCoachId(null);
        course.setChildIds(new ArrayList<>());

        int rows = courseMapper.insert(course);
        if (rows <= 0) {
            throw new ServiceException("创建课程失败，请联系工作人员");
        }

        return course.getCourseId();
    }

    @Override
    public void assignCoach(Long courseId, Long coachId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if ("1".equals(course.getStatus())) {
            throw new ServiceException("课程已完成，无法更换教练");
        }

        validateCoach(coachId, storeId);

        // 校验教练时间冲突（排除当前课程）
        LocalTime startTime = course.getStartTime().toLocalTime();
        LocalTime endTime = startTime.plusHours(course.getTotalHours());
        checkCoachTimeConflict(coachId, course.getCourseDate(), startTime, endTime, courseId);

        course.setCoachId(coachId);

        int rows = courseMapper.updateById(course);
        if (rows <= 0) {
            throw new ServiceException("给课程分配教练失败，请联系工作人员");
        }
    }

    @Override
    public void cancelCourse(Long courseId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if ("1".equals(course.getStatus())) {
            throw new ServiceException("课程已完成，无法取消");
        }

        int rows = courseMapper.deleteById(courseId);
        if (rows <= 0) {
            throw new ServiceException("取消课程失败，请联系工作人员");
        }
    }

    @Override
    @Transactional
    public int arrangeChildren(Long courseId, List<Long> inputChildIds) {
        if (inputChildIds == null || inputChildIds.isEmpty()) {
            throw new ServiceException("请至少选择一个孩子");
        }

        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if ("1".equals(course.getStatus())) {
            throw new ServiceException("课程已完成，无法安排学员");
        }

        List<Long> existingChildIds = course.getChildIds();
        if (existingChildIds == null) {
            existingChildIds = new ArrayList<>();
        }

        LocalTime startTime = course.getStartTime().toLocalTime();
        LocalTime endTime = startTime.plusHours(course.getTotalHours());

        for (Long childId : inputChildIds) {
            // 校验孩子是否存在且正常
            Children child = childrenMapper.selectById(childId);
            if (child == null) {
                throw new ServiceException("孩子(ID:" + childId + ")信息不存在");
            }
            if ("1".equals(child.getStatus())) {
                throw new ServiceException("孩子「" + child.getChildName() + "」状态异常");
            }

            // 校验孩子的家长是否属于当前店铺
            User parent = userMapper.selectById(child.getParentId());
            if (parent == null || !storeId.equals(parent.getStoreId())) {
                throw new ServiceException("孩子「" + child.getChildName() + "」的家长不属于当前店铺会员");
            }

            // 校验是否已安排
            if (existingChildIds.contains(childId)) {
                throw new ServiceException("孩子「" + child.getChildName() + "」已安排在此课程中");
            }

            // 校验孩子在同一时间段是否有其他课程
            checkChildTimeConflict(childId, course.getCourseDate(), startTime, endTime, courseId);

            existingChildIds.add(childId);
        }

        course.setChildIds(existingChildIds);
        return courseMapper.updateById(course);
    }

    @Override
    @Transactional
    public int cancelChild(Long courseId, Long childId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if ("1".equals(course.getStatus())) {
            throw new ServiceException("课程已完成，无法取消安排");
        }

        List<Long> childIds = course.getChildIds();
        if (childIds == null || !childIds.contains(childId)) {
            throw new ServiceException("该孩子未安排在此课程中");
        }

        childIds.remove(childId);
        course.setChildIds(childIds);
        return courseMapper.updateById(course);
    }

    @Override
    public List<Course> listCourses(Long storeId, LocalDate courseDate) {
        if (storeId == null) {// 店铺管理员需要获取StoreId
            storeId = getStoreId();
        }

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStoreId, storeId);
        if (courseDate != null) {
            wrapper.eq(Course::getCourseDate, courseDate);
        }
        wrapper.orderByAsc(Course::getCourseDate)
                .orderByAsc(Course::getStartTime);
        return courseMapper.selectList(wrapper);
    }

    @Override
    public Course getCourseById(Long courseId) {
        Long storeId = getStoreId();
        return getCourseAndValidate(courseId, storeId);
    }

    // ============ 私有工具方法 ============

    /**
     * 通过ContextHolder获取当前管理员所在店铺ID
     * 
     * @return storeId
     */
    private Long getStoreId() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }
        return storeId;
    }

    /**
     * 获取课程并校验归属
     */
    private Course getCourseAndValidate(Long courseId, Long storeId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }
        if (!course.getStoreId().equals(storeId) && ContextHolder.getAO().getAdminInfo().getAdminType().equals("1")) {
            throw new ServiceException("无权操作其他店铺的课程");
        }
        return course;
    }

    /**
     * 校验教练合法性
     */
    private void validateCoach(Long coachId, Long storeId) {
        User coach = userMapper.selectById(coachId);
        if (coach == null) {
            throw new ServiceException("教练不存在");
        }
        if (!coach.isCoach()) {
            throw new ServiceException("指定用户不是教练");
        }
        if (!storeId.equals(coach.getStoreId())) {
            throw new ServiceException("该教练不属于当前店铺");
        }
        if ("1".equals(coach.getStatus())) {
            throw new ServiceException("该教练账号已停用");
        }
    }

    /**
     * 校验同一场地同一时间段是否有冲突
     */
    private void checkTimeConflict(Long courtId, LocalDate courseDate, LocalTime startTime, LocalTime endTime,
            Long excludeCourseId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCourtId, courtId)
                .eq(Course::getCourseDate, courseDate);
        if (excludeCourseId != null) {
            wrapper.ne(Course::getCourseId, excludeCourseId);
        }

        List<Course> existingCourses = courseMapper.selectList(wrapper);
        for (Course existing : existingCourses) {
            LocalTime existStart = existing.getStartTime().toLocalTime();
            LocalTime existEnd = existStart.plusHours(existing.getTotalHours());
            // 时间段重叠判断
            if (startTime.isBefore(existEnd) && endTime.isAfter(existStart)) {
                throw new ServiceException("该场地在" + existStart + "-" + existEnd + "时间段已有课程安排");
            }
        }
    }

    /**
     * 校验教练在同一时间段是否有冲突
     */
    private void checkCoachTimeConflict(Long coachId, LocalDate courseDate, LocalTime startTime, LocalTime endTime,
            Long excludeCourseId) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getCoachId, coachId)
                .eq(Course::getCourseDate, courseDate);
        if (excludeCourseId != null) {
            wrapper.ne(Course::getCourseId, excludeCourseId);
        }

        List<Course> existingCourses = courseMapper.selectList(wrapper);
        for (Course existing : existingCourses) {
            LocalTime existStart = existing.getStartTime().toLocalTime();
            LocalTime existEnd = existStart.plusHours(existing.getTotalHours());
            if (startTime.isBefore(existEnd) && endTime.isAfter(existStart)) {
                throw new ServiceException("该教练在" + existStart + "-" + existEnd + "时间段已有课程安排");
            }
        }
    }

    /**
     * 校验孩子在同一时间段是否有其他课程
     */
    private void checkChildTimeConflict(Long childId, LocalDate courseDate, LocalTime startTime, LocalTime endTime,
            Long excludeCourseId) {
        Long storeId = getStoreId();
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStoreId, storeId)
                .eq(Course::getCourseDate, courseDate);
        if (excludeCourseId != null) {
            wrapper.ne(Course::getCourseId, excludeCourseId);
        }

        List<Course> existingCourses = courseMapper.selectList(wrapper);
        for (Course existing : existingCourses) {
            if (existing.getChildIds() != null && existing.getChildIds().contains(childId)) {
                LocalTime existStart = existing.getStartTime().toLocalTime();
                LocalTime existEnd = existStart.plusHours(existing.getTotalHours());
                if (startTime.isBefore(existEnd) && endTime.isAfter(existStart)) {
                    throw new ServiceException("该孩子在" + existStart + "-" + existEnd + "时间段已有课程安排");
                }
            }
        }
    }
}
