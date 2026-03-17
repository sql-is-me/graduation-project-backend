package com.sql.transaction.dto;

import lombok.Data;

@Data
public class OrderCancelDTO {
    /**
     * 取消原因
     */
    private String cancelReason;
}
