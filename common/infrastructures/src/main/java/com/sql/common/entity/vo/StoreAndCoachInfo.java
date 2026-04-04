package com.sql.common.entity.vo;

import java.util.List;

import com.sql.common.entity.bo.CoachBriefInfo;
import com.sql.common.entity.bo.ManagerBriefInfo;
import com.sql.common.entity.bo.StoreBriefInfo;

import lombok.Data;

/**
 * 店铺详情 VO，含店铺基本信息、管理员列表及教练列表
 */
@Data
public class StoreAndCoachInfo {

    /** 店铺ID */
    private Long storeId;

    /** 店铺名称 */
    private String storeName;

    /** 店铺地址 */
    private String address;

    /** 本店铺管理员列表 */
    private List<ManagerBriefInfo> managers;

    /** 本店铺教练列表 */
    private List<CoachBriefInfo> coaches;

    public StoreAndCoachInfo(StoreBriefInfo brief, List<ManagerBriefInfo> managers, List<CoachBriefInfo> coaches) {
        this.storeId = brief.getStoreId();
        this.storeName = brief.getStoreName();
        this.address = brief.getAddress();
        this.managers = managers;
        this.coaches = coaches;
    }
}
