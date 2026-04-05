package com.sql.admin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.admin.mapper.CourseMapper;
import com.sql.admin.mapper.RequestMapper;
import com.sql.admin.mapper.TeachingPlanMapper;
import com.sql.admin.mapper.TrainingMethodMapper;
import com.sql.admin.mapper.UserMapper;
import com.sql.admin.service.RequestService;
import com.sql.common.constants.RequestConstants;
import com.sql.common.entity.bo.AdminOnline;
import com.sql.common.entity.po.Admin;
import com.sql.common.entity.po.Course;
import com.sql.common.entity.po.Request;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;

/**
 * 审批服务实现（管理员端）
 */
@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private TeachingPlanMapper teachingPlanMapper;

    @Autowired
    private TrainingMethodMapper trainingMethodMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Request> listPendingRequests() {
        Admin admin = currentAdmin();
        if (admin.isSysAdmin()) {
            return requestMapper.selectPendingSysAdmin();
        }
        Long storeId = requireStoreId(admin);
        return requestMapper.selectPendingByStore(storeId);
    }

    @Override
    @Transactional
    public void approve(Long requestId) {
        Admin admin = currentAdmin();
        Request req = getRequestOrThrow(requestId);

        if (!RequestConstants.STATUS_PENDING.equals(req.getStatus())) {
            throw new ServiceException("该请求已处理，无法重复审批");
        }

        if (isApprover1(admin, req)) {
            if (!RequestConstants.APPROVER_PENDING.equals(req.getApprover1Status())) {
                throw new ServiceException("您已审批过该请求");
            }
            requestMapper.updateApprover1Status(requestId, RequestConstants.APPROVER_APPROVED);
            req.setApprover1Status(RequestConstants.APPROVER_APPROVED);
        } else if (isApprover2(admin, req)) {
            if (!RequestConstants.APPROVER_PENDING.equals(req.getApprover2Status())) {
                throw new ServiceException("您已审批过该请求");
            }
            requestMapper.updateApprover2Status(requestId, RequestConstants.APPROVER_APPROVED);
            req.setApprover2Status(RequestConstants.APPROVER_APPROVED);
        } else {
            throw new ServiceException("您无权审批该请求");
        }

        if (allApproved(req)) {
            requestMapper.updateOverallStatus(requestId, RequestConstants.STATUS_APPROVED, null);
            onApproved(req);
        }
    }

    @Override
    @Transactional
    public void reject(Long requestId, String rejectReason) {
        Admin admin = currentAdmin();
        Request req = getRequestOrThrow(requestId);

        if (!RequestConstants.STATUS_PENDING.equals(req.getStatus())) {
            throw new ServiceException("该请求已处理，无法重复审批");
        }

        if (RequestConstants.VIP_LEAVE.equals(req.getType())) {
            throw new ServiceException("会员请假请求只能同意，不能拒绝");
        }

        if (isApprover1(admin, req)) {
            requestMapper.updateApprover1Status(requestId, RequestConstants.APPROVER_REJECTED);
        } else if (isApprover2(admin, req)) {
            requestMapper.updateApprover2Status(requestId, RequestConstants.APPROVER_REJECTED);
        } else {
            throw new ServiceException("您无权审批该请求");
        }

        requestMapper.updateOverallStatus(requestId, RequestConstants.STATUS_REJECTED, rejectReason);
        onRejected(req);
    }

    // ============ 审批通过后的业务处理 ============

    private void onApproved(Request req) {
        switch (req.getType()) {
            case RequestConstants.VIP_LEAVE -> handleLeaveApproved(req);
            case RequestConstants.COACH_UPLOAD_TEACHING_PLAN -> handleTeachingPlanApproved(req);
            case RequestConstants.COACH_UPLOAD_TRAINING_METHOD -> handleTrainingMethodApproved(req);
            case RequestConstants.VIP_BIND_STORE, RequestConstants.COACH_BIND_STORE -> handleBindStoreApproved(req);
            default -> throw new ServiceException("未知的请求类型：" + req.getType());
        }
    }

    /**
     * 会员请假通过：从课程安排中移除该孩子
     * payload: {"courseId":1, "childId":2}
     */
    private void handleLeaveApproved(Request req) {
        Map<String, Object> payload = req.getPayload();
        Long courseId = toLong(payload.get("courseId"));
        Long childId = toLong(payload.get("childId"));

        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new ServiceException("关联的课程不存在");
        }

        List<Long> childIds = course.getChildIds();
        if (childIds != null && childIds.contains(childId)) {
            childIds.remove(childId);
            course.setChildIds(childIds);
            courseMapper.updateById(course);
        }
    }

    /**
     * 教案审核通过
     * payload: {"tpId":1, "fileUrl":"/abs/path.pdf"}
     */
    private void handleTeachingPlanApproved(Request req) {
        Long tpId = toLong(req.getPayload().get("tpId"));
        teachingPlanMapper.updateStatus(tpId, "1", null);
    }

    /**
     * 训练方法审核通过
     * payload: {"tmId":1, "fileUrl":"/abs/path.pdf"}
     */
    private void handleTrainingMethodApproved(Request req) {
        Long tmId = toLong(req.getPayload().get("tmId"));
        trainingMethodMapper.updateStatus(tmId, "1", null);
    }

    /**
     * 绑定店铺审核通过：将用户绑定到目标店铺
     * payload: {"targetStoreId":1}
     */
    private void handleBindStoreApproved(Request req) {
        Long targetStoreId = toLong(req.getPayload().get("targetStoreId"));

        com.sql.common.entity.po.User user = userMapper.selectById(req.getSenderId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        user.setStoreId(targetStoreId);
        userMapper.updateById(user);
    }

    // ============ 审批拒绝后的业务处理 ============

    private void onRejected(Request req) {
        Map<String, Object> payload = req.getPayload();
        switch (req.getType()) {
            case RequestConstants.COACH_UPLOAD_TEACHING_PLAN -> {
                Long tpId = toLong(payload.get("tpId"));
                teachingPlanMapper.updateStatus(tpId, "2", req.getRejectReason());
            }
            case RequestConstants.COACH_UPLOAD_TRAINING_METHOD -> {
                Long tmId = toLong(payload.get("tmId"));
                trainingMethodMapper.updateStatus(tmId, "2", req.getRejectReason());
            }
            default -> {
            }
        }
    }

    // ============ 权限判断工具方法 ============

    private boolean isApprover1(Admin admin, Request req) {
        if (admin.isSysAdmin())
            return false;
        Long storeId = admin.getStoreId();
        return storeId != null && storeId.equals(req.getApprover1Id());
    }

    private boolean isApprover2(Admin admin, Request req) {
        if (!admin.isSysAdmin())
            return false;
        return req.getApprover2Status() != null;
    }

    private boolean allApproved(Request req) {
        if (!RequestConstants.APPROVER_APPROVED.equals(req.getApprover1Status())) {
            return false;
        }
        if (req.getApprover2Status() != null
                && !RequestConstants.APPROVER_APPROVED.equals(req.getApprover2Status())) {
            return false;
        }
        return true;
    }

    private Admin currentAdmin() {
        AdminOnline ao = ContextHolder.getAO();
        return ao.getAdminInfo();
    }

    private Long requireStoreId(Admin admin) {
        Long storeId = admin.getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }
        return storeId;
    }

    private Request getRequestOrThrow(Long requestId) {
        Request req = requestMapper.selectById(requestId);
        if (req == null) {
            throw new ServiceException("请求不存在");
        }
        return req;
    }

    private Long toLong(Object value) {
        if (value == null) {
            throw new ServiceException("请求载荷缺少必要字段");
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }
}
