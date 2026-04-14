package com.sql.common.entity.bo;

import java.io.Serializable;
import lombok.Data;

/**
 * 绑定店铺邀请码缓存对象
 */
@Data
public class BindStoreBody implements Serializable {
    /** 推荐人ID */
    private Long referrerId;

    /** 门店ID */
    private Long storeId;

    public BindStoreBody(Long referrerId, Long storeId) {
        this.referrerId = referrerId;
        this.storeId = storeId;
    }
}
