package com.sql.common.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("requests")
public class Request {
    @TableId(value = "request_id", type = IdType.AUTO)
    private Long requestId;

    /**
     * 发送方ID
     */
    private Long sender;

    /**
     * 接收方
     * 当接收方为店铺级别时，该字段存储为storeId
     * 当接受方为教练级别时，该字段为userId
     * 当接受方为系统级别时，该字段为null
     */
    private Long receiver;

    /**
     * 请求类型
     */
    private String type;

    /**
     * 请求状态
     * 0-待处理
     * 1-已同意
     * 2-已拒绝
     */
    private String status;

    /**
     * 备注
     */
    private String message;

}
