package com.sql.common.entity.dto;

import lombok.Data;

/**
 * 批量核销孩子出勤状态的单条记录
 */
@Data
public class VerifyChildDTO {
    /** 孩子ID */
    private Long childId;

    /**
     * 出勤状态
     * 1-正常完课 2-早退 3-缺勤
     */
    private String status;// TODO:迟到

    /** 备注（可选） */
    private String remark;
}
