package com.sql.user.service;

import java.util.List;

import com.sql.common.entity.po.Request;

/**
 * 审批请求服务接口（用户/教练端 —— 发起请求）
 */
public interface RequestService {

    /**
     * 会员发起请假请求
     * @param courseId 课程安排ID
     * @param childId  请假的孩子ID
     * @param message  请假说明
     */
    void submitLeave(Long courseId, Long childId, String message);

    /**
     * 通过邀请码申请绑定店铺
     * 若用户未绑定店铺（storeId 为 null），直接绑定
     * 若用户已绑定店铺，需原店铺管理员 + 系统管理员审批
     * @param inviteCode 邀请码
     * @param message    申请说明
     */
    void submitBindStore(String inviteCode, String message);

    /**
     * 查询当前用户发起的所有请求
     */
    List<Request> listMyRequests();
}
