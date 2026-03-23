package com.sql.common.vo;

import lombok.Data;
import java.time.LocalDateTime;

import com.sql.common.entity.db.OperLog;

@Data
public class OperLogInfo {
    /**
     * 操作日志ID
     */
    private Long operId;

    /**
     * 操作模块
     */
    private String title;

    /**
     * 业务类型（0=其它 1=新增 2=修改 3=删除 4=提取 5=授权）
     */
    private Integer businessType;

    /**
     * 操作人类别（0=顶级管理员 1=店铺管理员 2=教练 3=会员）
     */
    private Integer operatorType;

    /**
     * 操作人员名称
     */
    private String operatorName;

    /**
     * 操作IP地址
     */
    private String operIp;

    /**
     * 操作状态
     * 0=正常 1=异常
     */
    private String status;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;

    public OperLogInfo(OperLog operLog) {
        this.operId = operLog.getOperId();
        this.title = operLog.getTitle();
        this.businessType = operLog.getBusinessType();
        this.operatorType = operLog.getOperatorType();
        this.operatorName = operLog.getOperatorName();
        this.operIp = operLog.getOperIp();
        this.status = operLog.getStatus();
        this.operTime = operLog.getOperTime();
    }
}
