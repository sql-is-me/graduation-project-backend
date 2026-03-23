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

import com.sql.admin.dto.CourseCreateDTO;
import com.sql.admin.service.CourseService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.db.Course;
import com.sql.common.entity.result.R;
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
    @PostMapping("/create")
    public R<?> createCourse(@Validated @RequestBody CourseCreateDTO dto) {
        return R.ok(courseService.createCourse(dto), "课程创建成功");
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
     * 安排孩子上课（支持批量，1到多个）
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "课程管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @PutMapping("/{courseId}/children")
    public R<?> arrangeChildren(@PathVariable Long courseId, @RequestBody List<Long> childIds) { // TODO:待审查
        return R.ok(courseService.arrangeChildren(courseId, childIds), "学员安排成功");
    }

    /**
     * 取消孩子的上课安排
     */
    @RequiresType(UserTypes.MANAGER)
    @Log(title = "课程管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @DeleteMapping("/{courseId}/child/{childId}")
    public R<?> cancelChild(@PathVariable Long courseId, @PathVariable Long childId) { // TODO:待审查
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
        List<Course> list = courseService.listCourses(storeId, courseDate);
        return getDataTable(list);
    }

    /**
     * 查询课程详情
     */
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    @GetMapping("/{courseId}")
    public R<?> getCourseById(@PathVariable Long courseId) {
        return R.ok(courseService.getCourseById(courseId));
    }
}
