package com.sql.common.entity.bo;

import lombok.Data;

/**
 * 店铺基本信息（mapper 查询的中间结果）
 */
@Data
public class StoreBriefInfo {
    private Long storeId;
    private String storeName;
    private String address;
}
