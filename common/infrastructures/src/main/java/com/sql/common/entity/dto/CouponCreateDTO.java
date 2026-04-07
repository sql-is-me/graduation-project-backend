package com.sql.common.entity.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CouponCreateDTO {
    /**
     * 优惠券名称
     */
    @NotBlank(message = "优惠券名称不能为空")
    private String couponName;

    /**
     * 优惠券类型 0-满减券 1-折扣券
     */
    @NotBlank(message = "优惠券类型不能为空")
    private String couponType;

    /**
     * 优惠值(满减为金额,折扣为比例如0.8表示8折)
     */
    @NotNull(message = "优惠值不能为空")
    @DecimalMin(value = "0.01", message = "优惠值必须大于0")
    private BigDecimal discountValue;

    /**
     * 最低消费金额(满减门槛)
     */
    @NotNull(message = "最低消费金额不能为空")
    @DecimalMin(value = "0", message = "最低消费金额不能为负数")
    private BigDecimal minAmount;

    /**
     * 发放总量
     */
    @NotNull(message = "发放总量不能为空")
    @Min(value = 1, message = "发放总量至少为1")
    private Integer totalCount;

    /**
     * 生效开始时间
     */
    @NotNull(message = "生效开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    @NotNull(message = "生效结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 活动链接领券Token（可选）
     * 非空时该优惠券可通过活动链接领取，无需绑定店铺
     */
    private String linkToken;
}
