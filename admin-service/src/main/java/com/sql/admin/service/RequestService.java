package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.po.Request;

/**
 * 审批服务接口（管理员端）
 */
public interface RequestService {

    /**
     * 获取当前管理员待审批的请求列表
     * 店铺管理员：查询本店铺的待审请求
     * 系统管理员：查询所有待系统管理员审批的请求
     */
    List<Request> listPendingRequests();

    /**
     * 审批通过
     * @param requestId 请求ID
     */
    void approve(Long requestId);

    /**
     * 审批拒绝
     * @param requestId   请求ID
     * @param rejectReason 拒绝原因
     */
    void reject(Long requestId, String rejectReason);
}
