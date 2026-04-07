package com.sql.common.entity.bo;

import java.io.Serializable;
import lombok.Data;

/**
 * 教练邀请码缓存对象
 */
@Data
public class CoachInviteBody implements Serializable {
    /** 生成邀请码的管理员ID */
    private Long referrerId;

    /** 门店ID */
    private Long storeId;

    public CoachInviteBody(Long referrerIdId, Long storeId) {
        this.referrerId = referrerIdId;
        this.storeId = storeId;
    }
}
