package com.sql.admin.dto;

import lombok.Data;

@Data
public class OperLogSelectDTO {
    /**
     * 操作人类别（0=顶级管理员 1=店铺管理员 2=教练 3=会员）
     */
    private Integer operatorType;

    /**
     * 操作人员名称
     */
    private String operName;

    /**
     * 操作IP地址
     */
    private String operIp;

    /**
     * 操作状态
     * 0=正常 1=异常
     */
    private String status;
}
