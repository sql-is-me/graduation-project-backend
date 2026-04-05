package com.sql.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.common.constants.AuthConstants;
import com.sql.common.constants.RequestConstants;
import com.sql.common.entity.bo.CoachInviteBody;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.Children;
import com.sql.common.entity.po.Request;
import com.sql.common.entity.po.User;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.common.redis.service.RedisService;
import com.sql.user.mapper.ChildrenMapper;
import com.sql.user.mapper.RequestMapper;
import com.sql.user.mapper.UserMapper;
import com.sql.user.service.RequestService;

/**
 * 用户/教练请求服务实现（发起审批请求）
 */
@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChildrenMapper childrenMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public void submitLeave(Long courseId, Long childId, String message) {
        User user = currentUser();

        if (user.isCoach()) {
            throw new ServiceException("教练不能发起请假请求");
        }
        if (user.getStoreId() == null) {
            throw new ServiceException("您尚未绑定店铺，无法请假");
        }

        // 校验孩子属于当前用户
        Children child = childrenMapper.selectById(childId);
        if (child == null || !child.getParentId().equals(user.getUserId())) {
            throw new ServiceException("孩子信息不存在或不属于您");
        }

        // 构建 payload: {"courseId":1, "childId":2}
        Map<String, Object> payload = new HashMap<>();
        payload.put("courseId", courseId);
        payload.put("childId", childId);

        Request req = new Request();
        req.setSenderId(user.getUserId());
        req.setSenderType(RequestConstants.SENDER_TYPE_VIP);
        req.setType(RequestConstants.VIP_LEAVE);
        req.setPayload(payload);
        req.setMessage(message);
        req.setApprover1Id(user.getStoreId());
        req.setApprover1Status(RequestConstants.APPROVER_PENDING);

        int rows = requestMapper.insert(req);
        if (rows <= 0) {
            throw new ServiceException("请假申请提交失败");
        }
    }

    @Override
    public void submitBindStore(String inviteCode, String message) {
        User user = currentUser();

        // 验证邀请码并获取目标店铺ID
        String inviteKey = AuthConstants.INVITE_COACH_CODE + inviteCode;
        CoachInviteBody inviteBody = redisService.getCacheObject(inviteKey);
        if (inviteBody == null) {
            throw new ServiceException("邀请码无效或已过期");
        }

        Long targetStoreId = inviteBody.getStoreId();

        if (targetStoreId.equals(user.getStoreId())) {
            throw new ServiceException("您已绑定该店铺，无需重复绑定");
        }

        // 发送请求时即消耗邀请码
        redisService.deleteObject(inviteKey);
        String reverseKey = AuthConstants.INVITE_COACH + inviteBody.getReferrerIdId() + ":" + inviteBody.getStoreId();
        redisService.deleteObject(reverseKey);

        String senderType = user.isCoach()
                ? RequestConstants.SENDER_TYPE_COACH
                : RequestConstants.SENDER_TYPE_VIP;
        String requestType = user.isCoach()
                ? RequestConstants.COACH_BIND_STORE
                : RequestConstants.VIP_BIND_STORE;

        // 构建 payload: {"targetStoreId":1}
        Map<String, Object> payload = new HashMap<>();
        payload.put("targetStoreId", targetStoreId);

        if (user.getStoreId() == null) {
            // 未绑定店铺 → 直接绑定，无需审批
            user.setStoreId(targetStoreId);
            userMapper.updateById(user);
            return;
        }

        // 已有店铺 → 创建审批请求（原店铺管理员 + 系统管理员）
        Request req = new Request();
        req.setSenderId(user.getUserId());
        req.setSenderType(senderType);
        req.setType(requestType);
        req.setPayload(payload);
        req.setMessage(message);
        req.setApprover1Id(user.getStoreId());
        req.setApprover1Status(RequestConstants.APPROVER_PENDING);
        req.setApprover2Status(RequestConstants.APPROVER_PENDING);

        int rows = requestMapper.insert(req);
        if (rows <= 0) {
            throw new ServiceException("绑定店铺申请提交失败");
        }
    }

    @Override
    public List<Request> listMyRequests() {
        User user = currentUser();
        return requestMapper.selectBySenderId(user.getUserId());
    }

    private User currentUser() {
        UserOnline uo = ContextHolder.getUO();
        return uo.getUserInfo();
    }
}
