package com.sql.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sql.api.RemoteFileService;
import com.sql.common.constants.RequestConstants;
import com.sql.common.entity.bo.File;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.Request;
import com.sql.common.entity.po.TeachingPlan;
import com.sql.common.entity.po.TrainingMethod;
import com.sql.common.entity.po.User;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.MyTeachingPlanInfo;
import com.sql.common.entity.vo.MyTrainingMethodInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.user.dto.TeachingPlanUploadDTO;
import com.sql.user.dto.TrainingMethodUploadDTO;
import com.sql.user.mapper.RequestMapper;
import com.sql.user.mapper.TeachingPlanMapper;
import com.sql.user.mapper.TrainingMethodMapper;
import com.sql.user.service.DocService;
import com.sql.utils.StringUtils;

/**
 * 教练文档服务实现（教案 + 训练方法）
 */
@Service
public class DocServiceImpl implements DocService {

    @Autowired
    private TeachingPlanMapper teachingPlanMapper;

    @Autowired
    private TrainingMethodMapper trainingMethodMapper;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private RemoteFileService remoteFileService;

    @Value("${file.tp-path}")
    private String tpUrl;

    @Value("${file.tm-path}")
    private String tmUrl;

    @Override
    public void uploadTeachingPlan(TeachingPlanUploadDTO dto, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }

        UserOnline uo = ContextHolder.getUO();
        User coach = uo.getUserInfo();

        if (coach.getStoreId() == null) {
            throw new ServiceException("教练尚未绑定店铺，无法上传教案");
        }

        // 上传文件到 file-service，获取相对路径
        R<File> fileResult = remoteFileService.uploadTeachingPlan(file);
        if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
            throw new ServiceException("文件上传失败，请联系管理员");
        }
        String fileUrl = fileResult.getData().getUrl();

        // 入库
        TeachingPlan tp = new TeachingPlan();
        tp.setTitle(dto.getTitle());
        tp.setCoachId(coach.getUserId());
        tp.setStoreId(coach.getStoreId());
        tp.setFileUrl(fileUrl);
        tp.setDescription(dto.getDescription());
        // status 默认 "0" 待审核

        int rows = teachingPlanMapper.insert(tp);
        if (rows <= 0) {
            remoteFileService.deleteTeachingPlan(fileUrl);
            throw new ServiceException("教案保存失败，请联系管理员");
        }

        // 创建审核请求（approver1 = 所属店铺，这里存 storeId，由管理员侧按店铺查询）
        Request req = new Request();
        req.setSenderId(coach.getUserId());
        req.setSenderType("1"); // 教练
        req.setType(RequestConstants.COACH_UPLOAD_TEACHING_PLAN);
        req.setRefId(tp.getTpId());
        req.setApprover1Id(coach.getStoreId()); // 用 storeId 作为审核方标识
        req.setApprover1Status(RequestConstants.APPROVER_PENDING);

        rows = requestMapper.insert(req);
        if (rows <= 0) {
            throw new ServiceException("教案审核请求创建失败，请联系管理员");
        }
    }

    @Override
    public List<MyTeachingPlanInfo> listMyTeachingPlans() {
        UserOnline uo = ContextHolder.getUO();
        List<TeachingPlan> list = teachingPlanMapper.selectByCoachId(uo.getUserInfo().getUserId());
        return list.stream().map(MyTeachingPlanInfo::new).toList();
    }

    @Override
    public String getTeachingPlanFileUrl(Long tpId) {
        UserOnline uo = ContextHolder.getUO();
        TeachingPlan tp = teachingPlanMapper.selectById(tpId);
        if (tp == null) {
            throw new ServiceException("教案不存在");
        }
        // 只有归属本人的教案才可获取阅读链接
        if (!tp.getCoachId().equals(uo.getUserInfo().getUserId())) {
            throw new ServiceException("无权访问该教案");
        }
        return tpUrl + tp.getFileUrl();
    }

    @Override
    public void uploadTrainingMethod(TrainingMethodUploadDTO dto, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }

        UserOnline uo = ContextHolder.getUO();
        User coach = uo.getUserInfo();

        if (coach.getStoreId() == null) {
            throw new ServiceException("教练尚未绑定店铺，无法上传训练方法");
        }

        // 上传文件
        R<File> fileResult = remoteFileService.uploadTrainingMethod(file);
        if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
            throw new ServiceException("文件上传失败，请联系管理员");
        }
        String fileUrl = fileResult.getData().getUrl();

        // 入库
        TrainingMethod tm = new TrainingMethod();
        tm.setTitle(dto.getTitle());
        tm.setCoachId(coach.getUserId());
        tm.setStoreId(coach.getStoreId());
        tm.setFileUrl(fileUrl);
        tm.setDescription(dto.getDescription());
        // status 默认 "0" 待审核

        int rows = trainingMethodMapper.insert(tm);
        if (rows <= 0) {
            remoteFileService.deleteTrainingMethod(fileUrl);
            throw new ServiceException("训练方法保存失败，请联系管理员");
        }

        // 创建审核请求
        Request req = new Request();
        req.setSenderId(coach.getUserId());
        req.setSenderType("1"); // 教练
        req.setType(RequestConstants.COACH_UPLOAD_TRAINING_METHOD);
        req.setRefId(tm.getTmId());
        req.setApprover1Id(coach.getStoreId());
        req.setApprover1Status(RequestConstants.APPROVER_PENDING);

        rows = requestMapper.insert(req);
        if (rows <= 0) {
            throw new ServiceException("训练方法审核请求创建失败，请联系管理员");
        }
    }

    @Override
    public List<TrainingMethod> listStoreTrainingMethods() {
        UserOnline uo = ContextHolder.getUO();
        Long storeId = uo.getUserInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("教练尚未绑定店铺");
        }
        return trainingMethodMapper.selectApprovedByStoreId(storeId);
    }

    @Override
    public List<MyTrainingMethodInfo> listMyTrainingMethods() {
        UserOnline uo = ContextHolder.getUO();
        List<TrainingMethod> methods = trainingMethodMapper.selectByCoachId(uo.getUserInfo().getUserId());
        return methods.stream().map(MyTrainingMethodInfo::new).toList();
    }

    @Override
    public String getTrainingMethodFileUrl(Long tmId) {
        UserOnline uo = ContextHolder.getUO();
        TrainingMethod tm = trainingMethodMapper.selectById(tmId);
        if (tm == null) {
            throw new ServiceException("训练方法不存在");
        }
        // 需审核通过且属于同一店铺
        if (!"1".equals(tm.getStatus())) {
            throw new ServiceException("该训练方法尚未审核通过");
        }
        Long storeId = uo.getUserInfo().getStoreId();
        if (!tm.getStoreId().equals(storeId)) {
            throw new ServiceException("无权访问该训练方法");
        }
        return tmUrl + tm.getFileUrl();
    }
}
