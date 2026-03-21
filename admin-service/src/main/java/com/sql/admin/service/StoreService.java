package com.sql.admin.service;

import java.util.List;

import com.sql.admin.dto.StoreCreateDTO;
import com.sql.admin.dto.StoreUpdateDTO;
import com.sql.common.entity.db.Store;

public interface StoreService {
    /**
     * 创建店铺
     */
    Long createStore(StoreCreateDTO dto);

    /**
     * 更新店铺信息
     */
    void updateStore(Long storeId, StoreUpdateDTO dto);

    /**
     * 注销店铺（逻辑删除，设置状态为停业）
     */
    void deleteStore(Long storeId);

    /**
     * 设置店铺所有人（仅创建人可操作，目标必须为MANAGER）
     */
    void setOwner(Long storeId, Long ownerId);

    /**
     * 查询店铺列表
     * @param status 店铺状态筛选
     */
    List<Store> listStores(String status);

    /**
     * 根据ID查询店铺
     */
    Store getStoreById(Long storeId);
}
