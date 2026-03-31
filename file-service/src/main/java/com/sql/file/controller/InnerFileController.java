package com.sql.file.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.common.auth.annotation.InnerAuth;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.result.R;
import com.sql.file.service.FileService;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileUtils;

/**
 * 文件请求处理
 * 仅允许内部服务调用（通过Feign）
 */
@RestController
public class InnerFileController {
    private static final Logger log = LoggerFactory.getLogger(InnerFileController.class);

    @Autowired
    private FileService fileService;

    /**
     * 文件上传请求（仅内部调用）
     */
    @InnerAuth
    @PostMapping("/upload")
    public R<File> upload(@RequestPart(value = "file") MultipartFile mf) {
        try {
            // 上传并返回访问地址
            String url = fileService.uploadFile(mf);
            File file = new File();
            file.setName(FileUtils.getName(url));
            file.setUrl(url);
            return R.ok(file);
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 图片上传请求（仅内部调用，限5MB，仅jpg/jpeg/png）
     */
    @InnerAuth
    @PostMapping("/upload/pic")
    public R<File> uploadPicture(@RequestPart(value = "file") MultipartFile mf) {
        try {
            String url = fileService.uploadPicture(mf);
            File file = new File();
            file.setName(FileUtils.getName(url));
            file.setUrl(url);
            return R.ok(file);
        } catch (Exception e) {
            log.error("上传图片失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 文档上传请求（仅内部调用，限30MB，仅ppt/pdf/doc）
     */
    @InnerAuth
    @PostMapping("/upload/doc")
    public R<File> uploadDocument(@RequestPart(value = "file") MultipartFile mf) {
        try {
            String url = fileService.uploadDocument(mf);
            File file = new File();
            file.setName(FileUtils.getName(url));
            file.setUrl(url);
            return R.ok(file);
        } catch (Exception e) {
            log.error("上传文档失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 文件删除请求（仅内部调用）
     */
    @InnerAuth
    @DeleteMapping("/delete")
    public R<Boolean> delete(String fileUrl) {
        try {
            if (!FileUtils.validateFilePath(fileUrl)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            fileService.deleteFile(fileUrl);
            return R.ok();
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 图片删除请求（仅内部调用）
     */
    @InnerAuth
    @DeleteMapping("/delete/pic")
    public R<Boolean> deletePicture(String fileUrl) {
        try {
            if (!FileUtils.validateFilePath(fileUrl)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            fileService.deletePicture(fileUrl);
            return R.ok();
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 文档删除请求（仅内部调用）
     */
    @InnerAuth
    @DeleteMapping("/delete/doc")
    public R<Boolean> deleteDocument(String fileUrl) {
        try {
            if (!FileUtils.validateFilePath(fileUrl)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            fileService.deleteDocument(fileUrl);
            return R.ok();
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return R.fail(e.getMessage());
        }
    }
}
