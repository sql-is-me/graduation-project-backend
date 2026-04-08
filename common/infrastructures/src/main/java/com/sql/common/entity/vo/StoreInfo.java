package com.sql.common.entity.vo;

import java.time.LocalDateTime;

import com.sql.common.entity.po.Store;

import lombok.Data;

@Data
public class StoreInfo {
    /**
     * 店铺ID
     */
    private Long storeId;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 店铺创建者昵称
     */
    private String creatorName;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 店铺所有者ID
     */
    private Long ownerId;

    /**
     * 店铺所有者昵称
     */
    private String ownerName;

    /**
     * 店铺状态
     * 0-正常，1-停业
     */
    private String status; // 默认正常

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    public StoreInfo() {
    }

    public StoreInfo(Store store) {
        this.storeId = store.getStoreId();
        this.creatorId = store.getCreatorId();
        this.storeName = store.getStoreName();
        this.address = store.getAddress();
        this.ownerId = store.getOwnerId();
        this.status = store.getStatus();
        this.createTime = store.getCreateTime();
        this.updateTime = store.getUpdateTime();
    }
}
