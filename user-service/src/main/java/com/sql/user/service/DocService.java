package com.sql.user.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sql.common.entity.vo.TrainingMethodsInfo;
import com.sql.common.entity.vo.MyTeachingPlanInfo;
import com.sql.common.entity.vo.MyTrainingMethodInfo;
import com.sql.user.dto.TeachingPlanUploadDTO;
import com.sql.user.dto.TrainingMethodUploadDTO;

/**
 * 教练文档服务（教案 + 训练方法）
 */
public interface DocService {

    /**
     * 上传教案：保存文件 + 入库（状态0待审核）+ 创建审核请求
     */
    void uploadTeachingPlan(TeachingPlanUploadDTO dto, MultipartFile file);

    /**
     * 查询当前教练的所有教案
     */
    List<MyTeachingPlanInfo> listMyTeachingPlans();

    /**
     * 获取教案文件的可访问 URL（供 uniapp webview 在线阅读）
     * 仅限文档归属教练本人
     */
    String getTeachingPlanFileUrl(Long tpId);

    /**
     * 上传训练方法：保存文件 + 入库（状态0待审核）+ 创建审核请求
     */
    void uploadTrainingMethod(TrainingMethodUploadDTO dto, MultipartFile file);

    /**
     * 查询当前店铺已审核通过的所有训练方法（同店铺教练可见）
     */
    List<TrainingMethodsInfo> listStoreTrainingMethods();

    /**
     * 查询当前教练自己上传的所有训练方法
     */
    List<MyTrainingMethodInfo> listMyTrainingMethods();

    /**
     * 获取训练方法文件的可访问 URL（供 uniapp webview 在线阅读）
     * 同店铺教练均可访问已审核通过的训练方法
     */
    String getTrainingMethodFileUrl(Long tmId);
}
