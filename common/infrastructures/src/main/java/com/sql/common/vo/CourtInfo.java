package com.sql.common.vo;

import com.sql.common.entity.db.Court;

import lombok.Data;

@Data
public class CourtInfo {
    /**
     * 场地ID
     */
    private Long courtId;

    /**
     * 场地名称
     */
    private String courtName;

    /**
     * 所属店铺ID
     */
    private Long storeId;

    /**
     * 场地状态
     * 0-正常，1-维护中
     */
    private String status;

    public CourtInfo(Court court) {
        this.courtId = court.getCourtId();
        this.courtName = court.getCourtName();
        this.storeId = court.getStoreId();
        this.status = court.getStatus();
    }
}
