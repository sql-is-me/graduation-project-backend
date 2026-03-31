package com.sql.common.entity.po;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("coupons")
public class Coupon {
    @TableId(value = "coupon_id", type = IdType.AUTO)
    private Long couponId;

    /**
     * 优惠券名称
     */
    @TableField("coupon_name")
    private String couponName;

    /**
     * 所属店铺ID
     * 关联stores表
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 创建人ID
     * 关联admins表
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 优惠券类型
     * 0-满减券 1-折扣券
     */
    @TableField("coupon_type")
    private String couponType;

    /**
     * 优惠值
     * 满减为金额，折扣为比例(如0.8表示8折)
     */
    @TableField("discount_value")
    private BigDecimal discountValue;

    /**
     * 最低消费金额(满减门槛)
     */
    @TableField("min_amount")
    private BigDecimal minAmount;

    /**
     * 发放总量
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 剩余数量
     */
    @TableField("remaining_count")
    private Integer remainingCount;

    /**
     * 每人限领数量
     */
    @TableField("claim_limit")
    private Integer claimLimit;

    /**
     * 生效开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 状态
     * 0-正常 1-已停用
     */
    private String status;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
