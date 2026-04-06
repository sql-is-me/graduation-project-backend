package com.sql.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.sql.api.RemoteFileService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.service.CourseService;

@RestController
@RequestMapping("/user/course")
@LoginRequired
@RequiresType(UserTypes.COACH)
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 上传课程签到照片（一个课程一张）
     */
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
     * 上传课程签退照片（一个课程一张）
     */
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
}
