package com.sql.admin.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.CourseService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.dto.CourseCreateDTO;
import com.sql.common.entity.dto.VerifyChildDTO;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.CourseInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

@RestController
@RequestMapping("/admin/course")
@LoginRequired
public class CourseController extends BaseController {
    @Autowired
    private CourseService courseService;

    /**
     * 创建课程
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "课程管理", businessType = BusinessType.INSERT, operatorType = UserTypes.MANAGER)
    @PostMapping
    public R<?> createCourse(@Validated @RequestBody CourseCreateDTO dto) {
        return R.ok(courseService.createCourse(dto), "课程创建成功");
    }

    /**
     * 取消课程
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "课程管理", businessType = BusinessType.DELETE, operatorType = UserTypes.MANAGER)
    @DeleteMapping("/{courseId}")
    public R<?> cancelCourse(@PathVariable Long courseId) {
        courseService.cancelCourse(courseId);
        return R.ok("课程取消成功");
    }

    /**
     * 安排/更换教练
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "课程管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @PutMapping("/{courseId}/coach/{coachId}")
    public R<?> assignCoach(@PathVariable Long courseId, @PathVariable Long coachId) {
        courseService.assignCoach(courseId, coachId);
        return R.ok("教练安排成功");
    }

    /**
     * 安排孩子上课（支持批量，1到多个）
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "课程管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @PutMapping("/{courseId}/children")
    public R<?> arrangeChildren(@PathVariable Long courseId, @RequestBody List<Long> childIds) {
        return R.ok(courseService.arrangeChildren(courseId, childIds), "学员安排成功");
    }

    /**
     * 取消孩子的上课安排
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "课程管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @DeleteMapping("/{courseId}/child/{childId}")
    public R<?> cancelChild(@PathVariable Long courseId, @PathVariable Long childId) {
        return R.ok(courseService.cancelChild(courseId, childId), "取消安排成功");
    }

    /**
     * 查询课程列表（可按日期筛选）
     */
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    @GetMapping("/list")
    public TableDataInfo listCourses(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate courseDate,
            @RequestParam(required = false) Long storeId) {
        startPage();
        List<CourseInfo> list = courseService.listCourses(storeId, courseDate);
        return getDataTable(list);
    }

    /**
     * 查询课程详情
     *
     * @return CourseDetailedInfo,包含课程详情，教练详情，以及上课孩子详情和出勤详情
     */
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    @GetMapping("/{courseId}")
    public R<?> getCourseById(@PathVariable Long courseId) {
        return R.ok(courseService.getCourseById(courseId));
    }

    /**
     * 查询课程考勤信息（签到签退照片+时间，及每个孩子当前出勤状态）
     * 供管理员核销前查看
     */
    @RequiresType(UserTypes.MANAGER)
    @GetMapping("/{courseId}/attendanceInfo")
    public R<?> getCourseAttendanceInfo(@PathVariable Long courseId) {
        return R.ok(courseService.getCourseAttendanceInfo(courseId));
    }

    /**
     * 批量核销孩子出勤状态
     * 管理员对照签到签退照片，为每个孩子设置出勤状态
     * status: 1-正常完课 2-迟到 3-早退 4-缺勤
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "考勤核销", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @PostMapping("/{courseId}/verify")
    public R<?> batchVerify(@PathVariable Long courseId, @RequestBody List<VerifyChildDTO> items) {
        courseService.batchVerify(courseId, items);
        return R.ok("核销完成");
    }
}
