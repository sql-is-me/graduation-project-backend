package com.sql.file.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sql.file.service.FileService;
import com.sql.file.utils.FileUploadUtils;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileUtils;

/**
 * 本地文件存储
 */
@Primary
@Service
public class LocalFileServiceImpl implements FileService {
    private static final String DEFAULT_ADMIN_AVATAR = "/default_admin.jpg";
    private static final String DEFAULT_USER_AVATAR = "/default_user.jpg";
    /**
     * 域名或本机访问地址
     */
    @Value("${file.domain}")
    public String domain;

    /**
     * 通用文件存储在本地的根路径
     */
    @Value("${file.path}")
    private String localFilePath;

    /**
     * 通用文件资源映射路径前缀
     */
    @Value("${file.prefix}")
    public String localFilePrefix;

    /**
     * 图片资源映射路径前缀
     */
    @Value("${file.pic-prefix}")
    public String localPicPrefix;

    /**
     * 图片存储在本地的根路径
     */
    @Value("${file.pic-path}")
    private String localPicPath;

    /**
     * 文档存储在本地的根路径
     */
    @Value("${file.doc-path}")
    private String localDocPath;

    /**
     * 文档资源映射路径前缀
     */
    @Value("${file.doc-prefix}")
    public String localDocPrefix;

    /**
     * 本地文件上传接口
     * 
     * @param file 上传的文件
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        String name = FileUploadUtils.upload(localFilePath, file);
        String url = domain + localFilePrefix + name;
        return url;
    }

    /**
     * 本地图片上传接口
     * 
     * @param file 上传的图片
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadPicture(MultipartFile file) throws Exception {
        // 只返回相对路径，如 /20260322_xxx.jpg
        return FileUploadUtils.uploadPicture(localPicPath, file);
    }

    /**
     * 本地文档上传接口
     * 
     * @param file 上传的文档
     * @return 访问地址
     * @throws Exception
     */
    @Override
    public String uploadDocument(MultipartFile file) throws Exception {
        String name = FileUploadUtils.uploadDocument(localDocPath, file);
        String url = domain + localDocPrefix + name;
        return url;
    }

    /**
     * 本地文件删除接口
     * 
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    @Override
    public void deleteFile(String fileUrl) throws Exception {
        String localFile = StringUtils.substringAfter(fileUrl, localFilePrefix);
        FileUtils.deleteFile(localFilePath + localFile);
    }

    /**
     * 本地图片删除接口
     * 
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    @Override
    public void deletePicture(String fileUrl) throws Exception {
        // 默认照片跳过
        if (fileUrl.endsWith(DEFAULT_ADMIN_AVATAR) || fileUrl.endsWith(DEFAULT_USER_AVATAR)) {
            return;
        }
        // fileUrl 是相对路径
        FileUtils.deleteFile(localPicPath + fileUrl);
    }

    /**
     * 本地文档删除接口
     * 
     * @param fileUrl 文件访问URL
     * @throws Exception
     */
    @Override
    public void deleteDocument(String fileUrl) throws Exception {
        String localFile = StringUtils.substringAfter(fileUrl, localDocPrefix);
        FileUtils.deleteFile(localDocPath + localFile);
    }
}
