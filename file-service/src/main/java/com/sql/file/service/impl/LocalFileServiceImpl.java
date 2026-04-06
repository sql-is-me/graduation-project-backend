package com.sql.file.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sql.file.service.FileService;
import com.sql.file.utils.FileUploadUtils;
import com.sql.utils.file.FileUtils;

/**
 * 本地文件存储
 * 所有上传方法均返回相对路径（如 /20260322_xxx.jpg），删除时直接拼接本地根路径
 */
@Primary
@Service
public class LocalFileServiceImpl implements FileService {
    private static final String DEFAULT_ADMIN_AVATAR = "/default_admin.jpg";
    private static final String DEFAULT_USER_AVATAR = "/default_user.jpg";
    private static final String DEFAULT_CHILD_AVATAR = "/default_child.jpg";

    /**
     * 通用文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 头像存储在本地的根路径
     */
    @Value("${file.avatar-path}")
    private String localAvatarPath;

    /**
     * 签到/签退图片存储在本地的根路径
     */
    @Value("${file.sign-path}")
    private String localSignPath;

    /**
     * 教案存储在本地的根路径
     */
    @Value("${file.tp-path}")
    private String localTeachingPlanPath;

    /**
     * 训练方法存储在本地的根路径
     */
    @Value("${file.tm-path}")
    private String localTrainingMethodPath;

    /**
     * 孩子照片存储在本地的根路径
     */
    @Value("${file.child-photo-path}")
    private String localChildPhotoPath;

    /**
     * 本地文件上传
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        return FileUploadUtils.upload(localFilePath, file);
    }

    /**
     * 本地文件删除
     */
    @Override
    public void deleteFile(String fileUrl) throws Exception {
        FileUtils.deleteFile(localFilePath + fileUrl);
    }

    /**
     * 头像上传
     */
    @Override
    public String uploadAvatar(MultipartFile file) throws Exception {
        return FileUploadUtils.uploadAvatar(localAvatarPath, file);
    }

    /**
     * 头像删除
     */
    @Override
    public void deleteAvatar(String fileUrl) throws Exception {
        if (fileUrl.endsWith(DEFAULT_ADMIN_AVATAR) || fileUrl.endsWith(DEFAULT_USER_AVATAR)) {
            return;
        }
        FileUtils.deleteFile(localAvatarPath + fileUrl);
    }

    /**
     * 签到/签退图片上传
     */
    @Override
    public String uploadSignPicture(MultipartFile file) throws Exception {
        return FileUploadUtils.uploadSignPicture(localSignPath, file);
    }

    /**
     * 签到/签退图片删除
     */
    @Override
    public void deleteSignPicture(String fileUrl) throws Exception {
        FileUtils.deleteFile(localSignPath + fileUrl);
    }

    /**
     * 教案上传
     */
    @Override
    public String uploadTeachingPlan(MultipartFile file) throws Exception {
        return FileUploadUtils.uploadTeachingPlan(localTeachingPlanPath, file);
    }

    /**
     * 教案删除
     */
    @Override
    public void deleteTeachingPlan(String fileUrl) throws Exception {
        FileUtils.deleteFile(localTeachingPlanPath + fileUrl);
    }

    /**
     * 训练方法上传，返回相对路径
     */
    @Override
    public String uploadTrainingMethod(MultipartFile file) throws Exception {
        return FileUploadUtils.uploadTrainingMethod(localTrainingMethodPath, file);
    }

    /**
     * 训练方法删除
     */
    @Override
    public void deleteTrainingMethod(String fileUrl) throws Exception {
        FileUtils.deleteFile(localTrainingMethodPath + fileUrl);
    }

    /**
     * 孩子照片上传
     */
    @Override
    public String uploadChildPhoto(MultipartFile file) throws Exception {
        return FileUploadUtils.uploadChildPhoto(localChildPhotoPath, file);
    }

    /**
     * 孩子照片删除
     */
    @Override
    public void deleteChildPhoto(String fileUrl) throws Exception {
        if (fileUrl.endsWith(DEFAULT_CHILD_AVATAR)) {
            return;
        }
        FileUtils.deleteFile(localChildPhotoPath + fileUrl);
    }
}
