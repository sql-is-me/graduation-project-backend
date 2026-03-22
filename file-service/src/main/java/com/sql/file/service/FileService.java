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
     * 图片上传接口（jpg/jpeg/png，限5MB）
     *
     * @param file 上传的图片
     * @return 访问地址
     * @throws Exception
     */
    public String uploadPicture(MultipartFile file) throws Exception;

    /**
     * 文档上传接口（ppt/pdf/doc，限30MB）
     *
     * @param file 上传的文档
     * @return 访问地址
     * @throws Exception
     */
    public String uploadDocument(MultipartFile file) throws Exception;

    /**
     * 文件删除接口
     *
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    public void deleteFile(String fileUrl) throws Exception;

    /**
     * 文件删除接口
     *
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    public void deletePicture(String fileUrl) throws Exception;

    /**
     * 文件删除接口
     *
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    public void deleteDocument(String fileUrl) throws Exception;
}
