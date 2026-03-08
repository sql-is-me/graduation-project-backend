package com.ruoyi.admin.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 管理员邀请码缓存对象
 */
@Data
public class AdminInviteInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 推荐人ID */
    private Long referrerId;

    /** 门店ID */
    private Long storeId;
}
