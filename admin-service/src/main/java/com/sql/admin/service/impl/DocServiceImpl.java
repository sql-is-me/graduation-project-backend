package com.sql.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.admin.mapper.TeachingPlanMapper;
import com.sql.admin.mapper.TrainingMethodMapper;
import com.sql.admin.mapper.UserMapper;
import com.sql.admin.service.DocService;
import com.sql.common.entity.bo.AdminOnline;
import com.sql.common.entity.po.TeachingPlan;
import com.sql.common.entity.po.TrainingMethod;
import com.sql.common.entity.po.User;
import com.sql.common.entity.vo.TeachingPlanInfo;
import com.sql.common.entity.vo.TeachingPlansInfo;
import com.sql.common.entity.vo.TrainingMethodInfo;
import com.sql.common.entity.vo.TrainingMethodsInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.utils.file.FileUtils;

/**
 * 店铺文档服务实现
 */
@Service
public class DocServiceImpl implements DocService {

    @Autowired
    private TeachingPlanMapper teachingPlanMapper;

    @Autowired
    private TrainingMethodMapper trainingMethodMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取当前管理员所属店铺ID，系统管理员不允许直接调用文档接口
     */
    private Long currentStoreId() {
        AdminOnline ao = ContextHolder.getAO();
        Long storeId = ao.getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("您未绑定相关账户");
        }
        return storeId;
    }

    @Override
    public List<TeachingPlansInfo> listTeachingPlans() {
        return teachingPlanMapper.selectByStoreId(currentStoreId());
    }

    @Override
    public TeachingPlanInfo getTeachingPlan(Long tpId) {
        TeachingPlan tp = teachingPlanMapper.selectById(tpId);
        if (tp == null) {
            throw new ServiceException("教案不存在");
        }
        if (!tp.getStoreId().equals(currentStoreId())) {
            throw new ServiceException("无权查看该教案");
        }
        User coach = userMapper.selectById(tp.getCoachId());
        return new TeachingPlanInfo(tp, coach.getNickName());
    }

    @Override
    public String getTeachingPlanFileUrl(Long tpId) {
        TeachingPlan tp = teachingPlanMapper.selectById(tpId);
        // fileUrl 为相对路径如 /xxx.pdf，拼接对应 url 返回完整可访问地址
        return FileUtils.toAbsoluteUrl(FileUtils.TYPE_TP, tp.getFileUrl());
    }

    @Override
    public List<TrainingMethodsInfo> listTrainingMethods() {
        return trainingMethodMapper.selectByStoreId(currentStoreId());
    }

    @Override
    public TrainingMethodInfo getTrainingMethod(Long tmId) {
        TrainingMethod tm = trainingMethodMapper.selectById(tmId);
        if (tm == null) {
            throw new ServiceException("训练方法不存在");
        }
        if (!tm.getStoreId().equals(currentStoreId())) {
            throw new ServiceException("无权查看该训练方法");
        }
        User coach = userMapper.selectById(tm.getCoachId());
        return new TrainingMethodInfo(tm, coach.getNickName());
    }

    @Override
    public String getTrainingMethodFileUrl(Long tmId) {
        TrainingMethod tm = trainingMethodMapper.selectById(tmId);
        return FileUtils.toAbsoluteUrl(FileUtils.TYPE_TM, tm.getFileUrl());
    }
}
