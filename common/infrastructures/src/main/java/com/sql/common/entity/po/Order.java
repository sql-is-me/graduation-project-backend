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
@TableName("orders")
public class Order {
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     * 关联users表
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属店铺ID
     * 关联stores表
     */
    @TableField("store_id")
    private Long storeId;

    /**
     * 商品类型
     * 0-课时购买
     */
    @TableField("product_type")
    private String productType;

    /**
     * 购买数量(课时数)
     */
    private Integer quantity;

    /**
     * 单价(元)
     */
    @TableField("unit_price")
    private BigDecimal unitPrice;

    /**
     * 订单总金额(元)
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 优惠金额(元)
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 实付金额(元)
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 使用的用户优惠券ID
     * 关联user_coupons表
     */
    @TableField("coupon_id")
    private Long couponId;

    /**
     * 订单状态
     * 0-待支付 1-已支付 2-已取消 3-已退款
     */
    private String status;

    /**
     * 支付方式
     * wechat-微信支付
     */
    @TableField("pay_type")
    private String payType;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 微信支付交易号
     */
    @TableField("transaction_id")
    private String transactionId;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    @TableField("cancel_reason")
    private String cancelReason;

    @TableField(fill = FieldFill.INSERT, value = "create_time")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
    private LocalDateTime updateTime;
}
