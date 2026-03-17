package com.sql.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录实体类
 * 供顶级管理员查阅系统所有操作日志
 */
@Data
@TableName("operLog")
public class OperLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 日志主键
     */
    @TableId(value = "oper_id", type = IdType.AUTO)
    private Long operId;

    /**
     * 操作模块
     */
    private String title;

    /**
     * 业务类型（0=其它 1=新增 2=修改 3=删除 4=提取 5=授权）
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * 请求方法（类名.方法名）
     */
    private String method;

    /**
     * 请求方式（GET、POST、PUT、DELETE）
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 操作人类别（0=顶级管理员 1=店铺管理员 2=教练 3=会员）
     */
    @TableField("operator_type")
    private Integer operatorType;

    /**
     * 操作人员名称
     */
    @TableField("oper_name")
    private String operName;

    /**
     * 请求URL(0-255)
     */
    @TableField("oper_url")
    private String operUrl;

    /**
     * 操作IP地址
     */
    @TableField("oper_ip")
    private String operIp;

    /**
     * 请求参数
     */
    @TableField("oper_param")
    private String operParam;

    /**
     * 返回参数
     */
    @TableField("json_result")
    private String jsonResult;

    /**
     * 操作状态（0=正常 1=异常）
     */
    private Integer status;

    /**
     * 错误消息
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("oper_time")
    private Date operTime;

    /**
     * 消耗时间（毫秒）
     */
    @TableField("cost_time")
    private Long costTime;
}
