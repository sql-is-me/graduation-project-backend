package com.sql.api.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sql.api.RemoteFileService;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.result.R;

/**
 * 文件服务降级处理
 */
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteFileFallbackFactory.class);

    @Override
    public RemoteFileService create(Throwable throwable) {
        log.error("文件服务调用失败:{}", throwable.getMessage());
        return new RemoteFileService() {
            @Override
            public R<File> upload(MultipartFile file) {
                return R.fail("上传文件失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> delete(String fileUrl) {
                return R.fail("删除文件失败:" + throwable.getMessage());
            }

            @Override
            public R<File> uploadAvatar(MultipartFile file) {
                return R.fail("上传头像失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> deleteAvatar(String fileUrl) {
                return R.fail("删除头像失败:" + throwable.getMessage());
            }

            @Override
            public R<File> uploadSignPicture(MultipartFile file) {
                return R.fail("上传签到图片失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> deleteSignPicture(String fileUrl) {
                return R.fail("删除签到图片失败:" + throwable.getMessage());
            }

            @Override
            public R<File> uploadTeachingPlan(MultipartFile file) {
                return R.fail("上传教案失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> deleteTeachingPlan(String fileUrl) {
                return R.fail("删除教案失败:" + throwable.getMessage());
            }

            @Override
            public R<File> uploadTrainingMethod(MultipartFile file) {
                return R.fail("上传训练方法失败:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> deleteTrainingMethod(String fileUrl) {
                return R.fail("删除训练方法失败:" + throwable.getMessage());
            }
        };
    }
}
