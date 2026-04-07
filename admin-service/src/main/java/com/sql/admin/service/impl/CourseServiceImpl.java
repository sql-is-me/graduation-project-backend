package com.sql.admin.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.ChildrenMapper;
import com.sql.admin.mapper.ClassHourMapper;
import com.sql.admin.mapper.CourseChildMapper;
import com.sql.admin.mapper.CourtMapper;
import com.sql.admin.mapper.CourseMapper;
import com.sql.admin.mapper.UserMapper;
import com.sql.admin.service.CourseService;
import com.sql.utils.file.FileUtils;
import com.sql.common.entity.dto.CourseCreateDTO;
import com.sql.common.entity.dto.VerifyChildDTO;
import com.sql.common.entity.po.Children;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.po.Course;
import com.sql.common.entity.po.AttendanceRecord;
import com.sql.common.entity.po.Court;
import com.sql.common.entity.po.User;
import com.sql.common.entity.vo.ChildAndAttendanceInfo;
import com.sql.common.entity.vo.CourseAttendanceInfo;
import com.sql.common.entity.vo.CourseDetailedInfo;
import com.sql.common.entity.vo.CourseInfo;
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

    @Autowired
    private ClassHourMapper classHourMapper;

    @Autowired
    private CourseChildMapper courseChildMapper;

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

        int rows = courseMapper.insert(course);
        if (rows <= 0) {
            throw new ServiceException("创建课程失败，请联系工作人员");
        }

        return course.getCourseId();
    }

    @Override
    @Transactional
    public void cancelCourse(Long courseId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if (!"0".equals(course.getStatus())) {
            throw new ServiceException("课程不处于准备状态，无法取消");
        }

        // 返还所有已安排孩子的家长课时（含已请假的孩子，请假仅标记状态不返还课时，统一在此处理）
        List<Long> childIds = course.getChildIds();
        if (childIds != null && !childIds.isEmpty()) {
            Map<Long, Integer> parentReturnMap = new HashMap<>();
            for (Long childId : childIds) {
                Children child = childrenMapper.selectById(childId);
                if (child != null) {
                    parentReturnMap.merge(child.getParentId(), course.getTotalHours(), Integer::sum);
                }
            }
            for (Map.Entry<Long, Integer> entry : parentReturnMap.entrySet()) {
                returnClassHours(entry.getKey(), entry.getValue());
            }
        }

        // 软删除：将课程状态置为已取消（3），保留出勤记录用于审计
        course.setStatus("3");
        int rows = courseMapper.updateById(course);
        if (rows <= 0) {
            throw new ServiceException("取消课程失败，请联系工作人员");
        }
    }

    @Override
    public void assignCoach(Long courseId, Long coachId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if (!"0".equals(course.getStatus())) {
            throw new ServiceException("课程不处于准备状态，无法更换教练");
        }

        // 校验教练合法性
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
    @Transactional
    public int arrangeChildren(Long courseId, List<Long> inputChildIds) {
        if (inputChildIds == null || inputChildIds.isEmpty()) {
            throw new ServiceException("请至少选择一个孩子");
        }

        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if (!"0".equals(course.getStatus())) {
            throw new ServiceException("课程不处于准备状态，无法安排学员");
        }

        List<Long> existingChildIds = course.getChildIds();
        if (existingChildIds == null) {
            existingChildIds = new ArrayList<>();
        }

        LocalTime startTime = course.getStartTime().toLocalTime();
        LocalTime endTime = startTime.plusHours(course.getTotalHours());

        int totalHours = course.getTotalHours();
        // 收集需要扣除课时的家长，key=parentId, value=需扣除课时数（同一家长多个孩子累加）
        Map<Long, Integer> parentDeductMap = new HashMap<>();
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
                throw new ServiceException("孩子「" + child.getChildName() + "」的家长不属于当前店铺");
            }

            // 校验是否已安排
            if (existingChildIds.contains(childId)) {
                throw new ServiceException("孩子「" + child.getChildName() + "」已安排在此课程中");
            }

            // 校验孩子在同一时间段是否有其他课程
            checkChildTimeConflict(childId, course.getCourseDate(), startTime, endTime, courseId);

            // 累计该家长需扣除的课时
            parentDeductMap.merge(child.getParentId(), totalHours, Integer::sum);

            existingChildIds.add(childId);
        }

        // 检查每位家长课时是否充足并临时扣除
        for (Map.Entry<Long, Integer> entry : parentDeductMap.entrySet()) {
            deductClassHours(entry.getKey(), entry.getValue());
        }

        // 为每个新安排的孩子创建出勤记录（默认"待出勤"）
        for (Long childId : inputChildIds) {
            AttendanceRecord record = new AttendanceRecord();
            record.setCourseId(courseId);
            record.setChildId(childId);
            courseChildMapper.insert(record);
        }

        course.setChildIds(existingChildIds);
        return courseMapper.updateById(course);
    }

    @Override
    @Transactional
    public int cancelChild(Long courseId, Long childId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        if (!"0".equals(course.getStatus())) {
            throw new ServiceException("课程不处于准备状态，无法取消安排");
        }

        List<Long> childIds = course.getChildIds();
        if (childIds == null || !childIds.contains(childId)) {
            throw new ServiceException("该孩子未安排在此课程中");
        }

        // 返还家长课时
        Children child = childrenMapper.selectById(childId);
        if (child != null) {
            returnClassHours(child.getParentId(), course.getTotalHours());
        }

        // 删除出勤记录
        LambdaQueryWrapper<AttendanceRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(AttendanceRecord::getCourseId, courseId).eq(AttendanceRecord::getChildId, childId);
        courseChildMapper.delete(recordWrapper);

        childIds.remove(childId);
        course.setChildIds(childIds);
        return courseMapper.updateById(course);
    }

    @Override
    public List<CourseInfo> listCourses(Long storeId, LocalDate courseDate) {
        if (storeId == null) {// 店铺管理员需要获取StoreId
            storeId = getStoreId();
        }

        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStoreId, storeId);
        if (courseDate != null) {
            wrapper.eq(Course::getCourseDate, courseDate);
        } else {
            wrapper.orderByAsc(Course::getCourseDate);
        }
        wrapper.orderByAsc(Course::getStartTime);
        List<Course> courses = courseMapper.selectList(wrapper);

        List<CourseInfo> courseInfos = new ArrayList<>();
        for (Course course : courses) {
            // 获取教练名称
            String coachName = null;
            if (course.getCoachId() != null) {
                User coach = userMapper.selectById(course.getCoachId());
                coachName = coach != null ? coach.getNickName() : null;
            }

            CourseInfo info = new CourseInfo(course, coachName);
            courseInfos.add(info);
        }
        return courseInfos;
    }

    @Override
    public CourseDetailedInfo getCourseById(Long courseId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        // 获取教练信息
        String coachName = null;
        String coachAvatar = null;
        if (course.getCoachId() != null) {
            User coach = userMapper.selectById(course.getCoachId());
            coachName = coach != null ? coach.getNickName() : null;
            coachAvatar = coach != null ? FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, coach.getAvatar()) : null;
        }

        // 获取孩子信息
        List<ChildAndAttendanceInfo> caInfos = new ArrayList<>();

        // 获取孩子id
        List<Long> childIds = course.getChildIds();
        // 获取出勤情况
        List<AttendanceRecord> attendanceRecords = courseChildMapper
                .selectList(new LambdaQueryWrapper<AttendanceRecord>()
                        .eq(AttendanceRecord::getCourseId, courseId));

        for (Long childId : childIds) {
            Children child = childrenMapper.selectById(childId);
            if (child != null) {
                ChildAndAttendanceInfo caInfo = new ChildAndAttendanceInfo();
                caInfo.setChildId(childId);
                caInfo.setChildName(child.getChildName());
                caInfo.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_CHILD_PHOTO, child.getPhoto()));

                // 从 attendanceRecords 中查找当前孩子的出勤记录
                String status = attendanceRecords
                        .stream()
                        .filter(record -> record.getChildId().equals(childId))
                        .map(AttendanceRecord::getStatus)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException(
                                "未找到ID:" + childId + "，姓名:" + child.getChildName() + "的孩子的出勤记录"));
                caInfo.setStatus(status);

                caInfos.add(caInfo);
            }
        }

        CourseDetailedInfo info = new CourseDetailedInfo(course);
        info.setCoachName(coachName);
        info.setCoachAvatar(coachAvatar);
        info.setChildAndAttendanceInfos(caInfos);

        return info;
    }

    @Override
    public CourseAttendanceInfo getCourseAttendanceInfo(Long courseId) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        // 获取所有孩子的出勤记录
        List<AttendanceRecord> records = courseChildMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecord>()
                        .eq(AttendanceRecord::getCourseId, courseId));

        List<ChildAndAttendanceInfo> caInfos = new ArrayList<>();
        List<Long> childIds = course.getChildIds();
        if (childIds != null) {
            for (Long childId : childIds) {
                Children child = childrenMapper.selectById(childId);
                if (child == null) continue;

                ChildAndAttendanceInfo caInfo = new ChildAndAttendanceInfo();
                caInfo.setChildId(childId);
                caInfo.setChildName(child.getChildName());
                caInfo.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_CHILD_PHOTO, child.getPhoto()));

                records.stream()
                        .filter(r -> r.getChildId().equals(childId))
                        .findFirst()
                        .ifPresent(r -> caInfo.setStatus(r.getStatus()));

                caInfos.add(caInfo);
            }
        }

        CourseAttendanceInfo info = new CourseAttendanceInfo(course);
        info.setSignInPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, course.getSignInPhoto()));
        info.setSignOutPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_SIGN, course.getSignOutPhoto()));
        info.setChildAndAttendanceInfos(caInfos);
        return info;
    }

    @Override
    @Transactional
    public void batchVerify(Long courseId, List<VerifyChildDTO> items) {
        Long storeId = getStoreId();
        Course course = getCourseAndValidate(courseId, storeId);

        // 课程必须处于已完成状态（签到签退照片均已上传）
        if (!"2".equals(course.getStatus())) {
            throw new ServiceException("课程尚未结束，无法核销（需等待教练上传签退照片）");
        }
        // 防止重复核销
        if ("1".equals(course.getVerifyStatus())) {
            throw new ServiceException("该课程已核销，无法重复操作");
        }

        Long adminId = ContextHolder.getAO().getAdminInfo().getAdminId();

        // 先统一处理已请假（status=5）的孩子：返还课时（请假仅标记，课时在此处统一返还）
        List<AttendanceRecord> allRecords = courseChildMapper.selectList(
                new LambdaQueryWrapper<AttendanceRecord>().eq(AttendanceRecord::getCourseId, courseId));
        for (AttendanceRecord record : allRecords) {
            if ("5".equals(record.getStatus())) {
                Children child = childrenMapper.selectById(record.getChildId());
                if (child != null) {
                    returnClassHours(child.getParentId(), course.getTotalHours());
                }
                record.setVerifyAdminId(adminId);
                record.setVerifyTime(LocalDateTime.now());
                courseChildMapper.updateById(record);
            }
        }

        for (VerifyChildDTO dto : items) {
            LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttendanceRecord::getCourseId, courseId)
                    .eq(AttendanceRecord::getChildId, dto.getChildId());
            AttendanceRecord record = courseChildMapper.selectOne(wrapper);

            if (record == null) {
                throw new ServiceException("孩子(ID:" + dto.getChildId() + ")不在此课程中");
            }
            // 已请假的由上方统一处理，跳过
            if ("5".equals(record.getStatus())) {
                continue;
            }
            // 已核销过的不重复处理
            if (!"0".equals(record.getStatus())) {
                continue;
            }

            String status = dto.getStatus();
            // 1-正常完课 2-迟到 3-早退 4-缺勤
            if (!"1".equals(status) && !"2".equals(status) && !"3".equals(status) && !"4".equals(status)) {
                throw new ServiceException("无效的出勤状态：" + status + "，仅支持 1-正常完课 2-迟到 3-早退 4-缺勤");
            }

            // 早退、缺勤返还课时；迟到不返还
            if ("3".equals(status) || "4".equals(status)) {
                Children child = childrenMapper.selectById(dto.getChildId());
                if (child != null) {
                    returnClassHours(child.getParentId(), course.getTotalHours());
                }
            }

            record.setStatus(status);
            record.setVerifyAdminId(adminId);
            record.setVerifyTime(LocalDateTime.now());
            courseChildMapper.updateById(record);
        }

        // 标记课程核销完成
        course.setVerifyStatus("1");
        courseMapper.updateById(course);
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

    /**
     * 检查家长课时是否充足并扣除
     */
    private void deductClassHours(Long parentId, int hours) {
        LambdaQueryWrapper<ClassHour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassHour::getUserId, parentId);
        ClassHour classHour = classHourMapper.selectOne(wrapper);

        if (classHour == null || classHour.getRemainingHours() < hours) {
            User parent = userMapper.selectById(parentId);
            String name = parent != null ? parent.getNickName() : "ID:" + parentId;

            throw new ServiceException("会员「" + name + "」课时不足（剩余"
                    + (classHour != null ? classHour.getRemainingHours() : 0)
                    + "课时，需要" + hours + "课时）");
        }

        classHour.setRemainingHours(classHour.getRemainingHours() - hours);
        classHour.setUsedHours(classHour.getUsedHours() + hours);
        classHourMapper.updateById(classHour);
    }

    /**
     * 返还家长课时（取消安排/取消课程时调用）
     */
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
