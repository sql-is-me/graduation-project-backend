package com.sql.user.service;

import com.sql.common.entity.vo.StoreAndCoachInfo;

/**
 * 店铺信息服务（用户侧）
 */
public interface StoreService {

    /**
     * 查询店铺详情（含管理员信息和教练列表）
     */
    StoreAndCoachInfo getStoreInfo(Long storeId);
}
