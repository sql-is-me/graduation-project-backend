package com.sql.user.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.api.RemoteFileService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.CourseInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.service.CourseService;
import com.sql.utils.BaseController;

@RestController
@RequestMapping("/user/course")
@LoginRequired
public class CourseController extends BaseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private RemoteFileService remoteFileService;

    // ===== COACH =====

    /**
     * 上传课程签到照片（标志课程开始，状态变为进行中）
     */
    @RequiresType(UserTypes.COACH)
    @Log(title = "考勤管理", businessType = BusinessType.INSERT, operatorType = UserTypes.COACH)
    @PostMapping(value = "/{courseId}/sign-in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<?> uploadSignIn(@PathVariable Long courseId,
            @RequestPart("file") MultipartFile file) {
        R<File> uploadResult = remoteFileService.uploadSignPicture(file);
        if (uploadResult == null || uploadResult.getCode() != 200) {
            return R.fail("签到照片上传失败");
        }
        courseService.uploadSignIn(courseId, uploadResult.getData().getUrl());
        return R.ok("签到照片上传成功");
    }

    /**
     * 上传课程签退照片（标志课程结束，状态变为已完成）
     */
    @RequiresType(UserTypes.COACH)
    @Log(title = "考勤管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.COACH)
    @PostMapping(value = "/{courseId}/sign-out", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<?> uploadSignOut(@PathVariable Long courseId,
            @RequestPart("file") MultipartFile file) {
        R<File> uploadResult = remoteFileService.uploadSignPicture(file);
        if (uploadResult == null || uploadResult.getCode() != 200) {
            return R.fail("签退照片上传失败");
        }
        courseService.uploadSignOut(courseId, uploadResult.getData().getUrl());
        return R.ok("签退照片上传成功");
    }

    /**
     * 查询教练自己的课程列表（可按日期筛选）
     */
    @RequiresType(UserTypes.COACH)
    @GetMapping("/coach/list")
    public TableDataInfo listCoachCourses(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate courseDate) {
        startPage();
        List<CourseInfo> list = courseService.listCoachCourses(courseDate);
        return getDataTable(list);
    }

    /**
     * 查询课程详情（教练视角）
     * 含课程基本信息、所有孩子出勤情况及签到签退照片
     */
    @RequiresType(UserTypes.COACH)
    @GetMapping("/coach/{courseId}")
    public R<?> getCoachCourseDetail(@PathVariable Long courseId) {
        return R.ok(courseService.getCoachCourseDetail(courseId));
    }

    // ===== VIP =====

    /**
     * 查询VIP自己孩子的课程列表（可按日期筛选）
     */
    @RequiresType(UserTypes.VIP)
    @GetMapping("/vip/list")
    public TableDataInfo listVipCourses(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate courseDate) {
        startPage();
        List<CourseInfo> list = courseService.listVipCourses(courseDate);
        return getDataTable(list);
    }

    /**
     * 查询课程详情（VIP视角）
     * 含签到签退照片，孩子出勤仅限当前VIP名下的孩子
     */
    @RequiresType(UserTypes.VIP)
    @GetMapping("/vip/{courseId}")
    public R<?> getVipCourseDetail(@PathVariable Long courseId) {
        return R.ok(courseService.getVipCourseDetail(courseId));
    }
}
