package com.sql.file.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sql.common.exception.file.FileException;
import com.sql.common.exception.file.FileNameLengthLimitExceededException;
import com.sql.common.exception.file.FileSizeLimitExceededException;
import com.sql.common.exception.file.InvalidExtensionException;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileTypeUtils;
import com.sql.utils.file.MimeTypeUtils;
import com.sql.utils.uuid.Seq;

/**
 * 文件上传工具类
 */
public class FileUploadUtils {
    /**
     * 默认最大大小 50M
     */
    public static final long DEFAULT_MAX_SIZE = 50 * 1024 * 1024L;

    /**
     * 图片最大大小 5M
     */
    public static final long PICTURE_MAX_SIZE = 5 * 1024 * 1024L;

    /**
     * 孩子照片最大大小 10M
     */
    public static final long PHOTO_MAX_SIZE = 10 * 1024 * 1024L;

    /**
     * 签到/签退图片最大大小 20M
     */
    public static final long SIGN_PICTURE_MAX_SIZE = 20 * 1024 * 1024L;

    /**
     * 教案最大大小 30M
     */
    public static final long TEACHING_PLAN_MAX_SIZE = 30 * 1024 * 1024L;

    /**
     * 文档最大大小 30M
     */
    public static final long DOCUMENT_MAX_SIZE = 30 * 1024 * 1024L;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    /**
     * 根据文件路径上传
     *
     * @param baseDir 相对应用的基目录
     * @param file    上传的文件
     * @return 上传成功的文件名
     * @throws FileSizeLimitExceededException       如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException                          比如读写文件出错时
     * @throws InvalidExtensionException            文件校验异常
     */
    public static final String upload(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, DEFAULT_MAX_SIZE, MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION);
        } catch (FileException fe) {
            throw new IOException(fe.getDefaultMessage(), fe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 头像上传（限制5MB，仅允许jpg/jpeg/png）
     */
    public static final String uploadAvatar(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, PICTURE_MAX_SIZE, MimeTypeUtils.AVATAR_EXTENSION);
        } catch (FileException fe) {
            throw new IOException(fe.getDefaultMessage(), fe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 签到/签退图片上传（限制20MB，仅允许jpg/jpeg/png）
     */
    public static final String uploadSignPicture(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, SIGN_PICTURE_MAX_SIZE, MimeTypeUtils.SIGN_EXTENSION);
        } catch (FileException fe) {
            throw new IOException(fe.getDefaultMessage(), fe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 教案上传（限制30MB，仅允许pdf/doc/docx）
     */
    public static final String uploadTeachingPlan(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, TEACHING_PLAN_MAX_SIZE, MimeTypeUtils.TEACHING_PLAN_EXTENSION);
        } catch (FileException fe) {
            throw new IOException(fe.getDefaultMessage(), fe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 训练方法上传（限制30MB，仅允许ppt/pptx/pdf/doc/docx）
     */
    public static final String uploadTrainingMethod(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, TEACHING_PLAN_MAX_SIZE, MimeTypeUtils.TRAINING_METHOD_EXTENSION);
        } catch (FileException fe) {
            throw new IOException(fe.getDefaultMessage(), fe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 教练照片上传（限制10MB，仅允许jpg/jpeg/png）
     */
    public static final String uploadCoachPhoto(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, PHOTO_MAX_SIZE, MimeTypeUtils.AVATAR_EXTENSION);
        } catch (FileException fe) {
            throw new IOException(fe.getDefaultMessage(), fe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 孩子照片上传（限制10MB，仅允许jpg/jpeg/png）
     */
    public static final String uploadChildPhoto(String baseDir, MultipartFile file) throws IOException {
        try {
            return upload(baseDir, file, PHOTO_MAX_SIZE, MimeTypeUtils.AVATAR_EXTENSION);
        } catch (FileException fe) {
            throw new IOException(fe.getDefaultMessage(), fe);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public static final String upload(String baseDir, MultipartFile file, long maxSize, String[] allowedExtension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException,
            InvalidExtensionException {
        int fileNamelength = Objects.requireNonNull(file.getOriginalFilename()).length();
        if (fileNamelength > FileUploadUtils.DEFAULT_FILE_NAME_LENGTH) {
            throw new FileNameLengthLimitExceededException(FileUploadUtils.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file, maxSize, allowedExtension);

        String fileName = extractFilename(file);

        String absPath = getAbsoluteFile(baseDir, fileName).getAbsolutePath();
        file.transferTo(Paths.get(absPath));
        return getPathFileName(fileName);
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename(MultipartFile file) {
        return StringUtils.format("{}_{}.{}", Seq.getId(Seq.uploadSeqType),
                FilenameUtils.getBaseName(file.getOriginalFilename()),
                FileTypeUtils.getExtension(file));
    }

    private static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc.isAbsolute() ? desc : desc.getAbsoluteFile();
    }

    private static final String getPathFileName(String fileName) throws IOException {
        String pathFileName = "/" + fileName;
        return pathFileName;
    }

    /**
     * 文件校验
     *
     * @param file             上传的文件
     * @param maxSize          文件最大限制
     * @param allowedExtension 文件后缀限制
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws InvalidExtensionException      文件后缀异常
     */
    public static final void assertAllowed(MultipartFile file, long maxSize, String[] allowedExtension)
            throws FileSizeLimitExceededException, InvalidExtensionException {
        long size = file.getSize();
        if (size > maxSize) {
            throw new FileSizeLimitExceededException(maxSize / 1024 / 1024);
        }

        String fileName = file.getOriginalFilename();
        String extension = FileTypeUtils.getExtension(file);
        if (allowedExtension != null && !isAllowedExtension(extension, allowedExtension)) {

            if (allowedExtension == MimeTypeUtils.AVATAR_EXTENSION) {
                throw new InvalidExtensionException.InvalidAvatarExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.DOCUMENT_EXTENSION) {
                throw new InvalidExtensionException.InvalidDocumentExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.IMAGE_EXTENSION) {
                throw new InvalidExtensionException.InvalidImageExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.MEDIA_EXTENSION) {
                throw new InvalidExtensionException.InvalidMediaExtensionException(allowedExtension, extension,
                        fileName);
            } else if (allowedExtension == MimeTypeUtils.VIDEO_EXTENSION) {
                throw new InvalidExtensionException.InvalidVideoExtensionException(allowedExtension, extension,
                        fileName);
            } else {
                throw new InvalidExtensionException(allowedExtension, extension, fileName);
            }
        }
    }

    /**
     * 判断MIME类型是否是允许的MIME类型
     *
     * @param extension        上传文件类型
     * @param allowedExtension 允许上传文件类型
     * @return true/false
     */
    public static final boolean isAllowedExtension(String extension, String[] allowedExtension) {
        for (String str : allowedExtension) {
            if (str.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}