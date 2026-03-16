package com.sql.admin.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 管理员邀请码缓存对象
 */
@Data
public class AdminInviteDTO implements Serializable {
    /** 推荐人ID */
    private Long referrerId;

    /** 门店ID */
    private Long storeId;

    public AdminInviteDTO(Long referrerId, Long storeId) {
        this.referrerId = referrerId;
        this.storeId = storeId;
    }
}
