package com.sql.user.service;

import java.util.List;

import com.sql.common.entity.bo.StoreBriefInfo;
import com.sql.common.entity.vo.StoreAndCoachInfo;

/**
 * 店铺信息服务（用户侧）
 */
public interface StoreService {

    /**
     * 查询店铺详情（含管理员信息和教练列表）
     */
    StoreAndCoachInfo getStoreInfo(Long storeId);

    /**
     * 模糊搜索店铺（按名称关键字）
     */
    List<StoreBriefInfo> searchStores(String keyword);
}
