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
     * 头像上传请求（仅内部调用，限5MB，仅jpg/jpeg/png）
     */
    @InnerAuth
    @PostMapping("/upload/avatar")
    public R<File> uploadAvatar(@RequestPart(value = "file") MultipartFile mf) {
        try {
            String url = fileService.uploadAvatar(mf);
            File file = new File();
            file.setName(FileUtils.getName(url));
            file.setUrl(url);
            return R.ok(file);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 头像删除请求（仅内部调用）
     */
    @InnerAuth
    @DeleteMapping("/delete/avatar")
    public R<Boolean> deleteAvatar(String fileUrl) {
        try {
            if (!FileUtils.validateFilePath(fileUrl)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            fileService.deleteAvatar(fileUrl);
            return R.ok();
        } catch (Exception e) {
            log.error("删除头像失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 签到/签退图片上传请求（仅内部调用，限20MB，仅jpg/jpeg/png）
     */
    @InnerAuth
    @PostMapping("/upload/sign")
    public R<File> uploadSignPicture(@RequestPart(value = "file") MultipartFile mf) {
        try {
            String url = fileService.uploadSignPicture(mf);
            File file = new File();
            file.setName(FileUtils.getName(url));
            file.setUrl(url);
            return R.ok(file);
        } catch (Exception e) {
            log.error("上传签到图片失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 签到/签退图片删除请求（仅内部调用）
     */
    @InnerAuth
    @DeleteMapping("/delete/sign")
    public R<Boolean> deleteSignPicture(String fileUrl) {
        try {
            if (!FileUtils.validateFilePath(fileUrl)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            fileService.deleteSignPicture(fileUrl);
            return R.ok();
        } catch (Exception e) {
            log.error("删除签到图片失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 教案上传请求（仅内部调用，限30MB，仅pdf/doc）
     */
    @InnerAuth
    @PostMapping("/upload/tp")
    public R<File> uploadTeachingPlan(@RequestPart(value = "file") MultipartFile mf) {
        try {
            String url = fileService.uploadTeachingPlan(mf);
            File file = new File();
            file.setName(FileUtils.getName(url));
            file.setUrl(url);
            return R.ok(file);
        } catch (Exception e) {
            log.error("上传教案失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 教案删除请求（仅内部调用）
     */
    @InnerAuth
    @DeleteMapping("/delete/tp")
    public R<Boolean> deleteTeachingPlan(String fileUrl) {
        try {
            if (!FileUtils.validateFilePath(fileUrl)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            fileService.deleteTeachingPlan(fileUrl);
            return R.ok();
        } catch (Exception e) {
            log.error("删除教案失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 训练方法上传请求（仅内部调用，限30MB，仅pdf/doc/docx/ppt/pptx）
     */
    @InnerAuth
    @PostMapping("/upload/tm")
    public R<File> uploadTrainingMethod(@RequestPart(value = "file") MultipartFile mf) {
        try {
            String url = fileService.uploadTrainingMethod(mf);
            File file = new File();
            file.setName(FileUtils.getName(url));
            file.setUrl(url);
            return R.ok(file);
        } catch (Exception e) {
            log.error("上传训练方法失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 训练方法删除请求（仅内部调用）
     */
    @InnerAuth
    @DeleteMapping("/delete/tm")
    public R<Boolean> deleteTrainingMethod(String fileUrl) {
        try {
            if (!FileUtils.validateFilePath(fileUrl)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许删除。 ", fileUrl));
            }
            fileService.deleteTrainingMethod(fileUrl);
            return R.ok();
        } catch (Exception e) {
            log.error("删除训练方法失败", e);
            return R.fail(e.getMessage());
        }
    }
}
