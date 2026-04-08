package com.sql.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 */
public interface FileService {
    /**
     * 文件上传接口
     * 
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    public String uploadFile(MultipartFile file) throws Exception;

    /**
     * 文件删除接口
     *
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    public void deleteFile(String fileUrl) throws Exception;

    /**
     * 头像上传接口（jpg/jpeg/png，限5MB）
     *
     * @param file 上传的头像
     * @return 相对路径
     * @throws Exception
     */
    public String uploadAvatar(MultipartFile file) throws Exception;

    /**
     * 头像删除接口
     *
     * @param fileUrl 头像相对路径
     * @throws Exception
     */
    public void deleteAvatar(String fileUrl) throws Exception;

    /**
     * 签到/签退图片上传接口（jpg/jpeg/png，限20MB）
     *
     * @param file 上传的图片
     * @return 相对路径
     * @throws Exception
     */
    public String uploadSignPicture(MultipartFile file) throws Exception;

    /**
     * 签到/签退图片删除接口
     *
     * @param fileUrl 图片相对路径
     * @throws Exception
     */
    public void deleteSignPicture(String fileUrl) throws Exception;

    /**
     * 教案上传接口（pdf/doc/docx，限30MB）
     *
     * @param file 上传的教案
     * @return 访问地址
     * @throws Exception
     */
    public String uploadTeachingPlan(MultipartFile file) throws Exception;

    /**
     * 教案删除接口
     *
     * @param fileUrl 教案访问URL
     * @throws Exception
     */
    public void deleteTeachingPlan(String fileUrl) throws Exception;

    /**
     * 训练方法上传接口（pdf/doc/docx/ppt/pptx，限30MB）
     *
     * @param file 上传的训练方法
     * @return 访问地址
     * @throws Exception
     */
    public String uploadTrainingMethod(MultipartFile file) throws Exception;

    /**
     * 训练方法删除接口
     *
     * @param fileUrl 训练方法访问URL
     * @throws Exception
     */
    public void deleteTrainingMethod(String fileUrl) throws Exception;

    /**
     * 教练照片上传接口（jpg/jpeg/png，限10MB）
     *
     * @param file 上传的照片
     * @return 相对路径
     * @throws Exception
     */
    public String uploadCoachPhoto(MultipartFile file) throws Exception;

    /**
     * 教练照片删除接口
     *
     * @param fileUrl 照片相对路径
     * @throws Exception
     */
    public void deleteCoachPhoto(String fileUrl) throws Exception;

    /**
     * 孩子照片上传接口（jpg/jpeg/png，限10MB）
     *
     * @param file 上传的照片
     * @return 相对路径
     * @throws Exception
     */
    public String uploadChildPhoto(MultipartFile file) throws Exception;

    /**
     * 孩子照片删除接口
     *
     * @param fileUrl 照片相对路径
     * @throws Exception
     */
    public void deleteChildPhoto(String fileUrl) throws Exception;

}
