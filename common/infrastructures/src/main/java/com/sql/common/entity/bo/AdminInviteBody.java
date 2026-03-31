package com.sql.common.entity.bo;

import java.io.Serializable;
import lombok.Data;

/**
 * 管理员邀请码缓存对象
 */
@Data
public class AdminInviteBody implements Serializable {
    /** 推荐人ID */
    private Long referrerId;

    /** 门店ID */
    private Long storeId;

    public AdminInviteBody(Long referrerId, Long storeId) {
        this.referrerId = referrerId;
        this.storeId = storeId;
    }
}
