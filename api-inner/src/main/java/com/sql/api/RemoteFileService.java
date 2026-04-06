package com.sql.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.sql.api.factory.RemoteFileFallbackFactory;
import com.sql.common.constants.ServiceNameConstants;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.result.R;

/**
 * 文件服务
 */
@FeignClient(contextId = "remoteFileService", value = ServiceNameConstants.FILE_SERVICE, fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteFileService {

    /**
     * 上传通用文件
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> upload(@RequestPart(value = "file") MultipartFile file);

    /**
     * 删除通用文件
     */
    @DeleteMapping(value = "/delete")
    R<Boolean> delete(@RequestParam String fileUrl);

    /**
     * 上传头像（jpg/jpeg/png，限5MB）
     */
    @PostMapping(value = "/upload/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> uploadAvatar(@RequestPart(value = "file") MultipartFile file);

    /**
     * 删除头像
     */
    @DeleteMapping(value = "/delete/avatar")
    R<Boolean> deleteAvatar(@RequestParam String fileUrl);

    /**
     * 上传签到/签退图片（jpg/jpeg/png，限20MB）
     */
    @PostMapping(value = "/upload/sign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> uploadSignPicture(@RequestPart(value = "file") MultipartFile file);

    /**
     * 删除签到/签退图片
     */
    @DeleteMapping(value = "/delete/sign")
    R<Boolean> deleteSignPicture(@RequestParam String fileUrl);

    /**
     * 上传教案（doc/docx/pdf，限30MB）
     */
    @PostMapping(value = "/upload/tp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> uploadTeachingPlan(@RequestPart(value = "file") MultipartFile file);

    /**
     * 删除教案
     */
    @DeleteMapping(value = "/delete/tp")
    R<Boolean> deleteTeachingPlan(@RequestParam String fileUrl);

    /**
     * 上传训练方法（ppt/pptx/doc/docx/pdf，限30MB）
     */
    @PostMapping(value = "/upload/tm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> uploadTrainingMethod(@RequestPart(value = "file") MultipartFile file);

    /**
     * 删除训练方法
     */
    @DeleteMapping(value = "/delete/tm")
    R<Boolean> deleteTrainingMethod(@RequestParam String fileUrl);

    /**
     * 上传孩子照片（jpg/jpeg/png，限10MB）
     */
    @PostMapping(value = "/upload/child-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<File> uploadChildPhoto(@RequestPart(value = "file") MultipartFile file);

    /**
     * 删除孩子照片
     */
    @DeleteMapping(value = "/delete/child-photo")
    R<Boolean> deleteChildPhoto(@RequestParam String fileUrl);
}
