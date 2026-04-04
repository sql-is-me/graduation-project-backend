package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.po.TeachingPlan;
import com.sql.common.entity.po.TrainingMethod;
import com.sql.common.entity.vo.TeachingPlanInfo;
import com.sql.common.entity.vo.TrainingMethodInfo;

/**
 * 店铺文档服务（教案 + 训练方法）
 * 供店铺管理员查看、审核本店铺文档使用
 */
public interface DocService {
    /**
     * 查询本店铺所有教案（所有状态）
     */
    List<TeachingPlanInfo> listTeachingPlans();

    /**
     * 查询教案详情
     */
    TeachingPlan getTeachingPlan(Long tpId);

    /**
     * 获取教案文件的可访问 URL（用于 Web 在线阅读/预览）
     */
    String getTeachingPlanFileUrl(Long tpId);

    /**
     * 查询本店铺所有训练方法（所有状态）
     */
    List<TrainingMethodInfo> listTrainingMethods();

    /**
     * 查询训练方法详情
     */
    TrainingMethod getTrainingMethod(Long tmId);

    /**
     * 获取训练方法文件的可访问 URL（用于 Web 在线阅读/预览）
     */
    String getTrainingMethodFileUrl(Long tmId);
}
