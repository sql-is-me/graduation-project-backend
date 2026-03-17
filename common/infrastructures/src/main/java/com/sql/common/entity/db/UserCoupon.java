package com.sql.common.entity.db;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_coupons")
public class UserCoupon {
    @TableId(value = "user_coupon_id", type = IdType.AUTO)
    private Long userCouponId;

    /**
     * 用户ID
     * 关联users表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 优惠券ID
     * 关联coupons表
     */
    @TableField("coupon_id")
    private Long couponId;

    /**
     * 状态
     * 0-未使用 1-已使用 2-已过期
     */
    private String status;

    /**
     * 使用的订单ID
     */
    @TableField("used_order_id")
    private Long usedOrderId;

    /**
     * 领取时间
     */
    @TableField("claim_time")
    private LocalDateTime claimTime;

    /**
     * 使用时间
     */
    @TableField("used_time")
    private LocalDateTime usedTime;
}
