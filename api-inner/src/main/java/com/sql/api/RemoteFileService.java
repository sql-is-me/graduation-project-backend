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
import com.sql.common.entity.File;
import com.sql.common.entity.result.R;

/**
 * 文件服务
 */
@FeignClient(contextId = "remoteFileService", value = ServiceNameConstants.FILE_SERVICE, fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteFileService {
    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<File> upload(@RequestPart(value = "file") MultipartFile file);

    /**
     * 上传图片（jpg/jpeg/png，限5MB）
     *
     * @param file 头像图片
     * @return 结果
     */
    @PostMapping(value = "/upload/pic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<File> uploadPicture(@RequestPart(value = "file") MultipartFile file);

    /**
     * 上传文档（ppt/pdf/doc，限30MB）
     *
     * @param file 文档信息
     * @return 结果
     */
    @PostMapping(value = "/upload/doc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<File> uploadDocument(@RequestPart(value = "file") MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件地址
     * @return 结果
     */
    @DeleteMapping(value = "/delete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public R<Boolean> delete(@RequestParam("fileUrl") String fileUrl);

    /**
     * 删除图片
     *
     * @param fileUrl 图片地址
     * @return 结果
     */
    @DeleteMapping(value = "/delete/pic", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public R<Boolean> deletePicture(@RequestParam("fileUrl") String fileUrl);

    /**
     * 删除文档
     *
     * @param fileUrl 文档地址
     * @return 结果
     */
    @DeleteMapping(value = "/delete/doc", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public R<Boolean> deleteDocument(@RequestParam("fileUrl") String fileUrl);
}
