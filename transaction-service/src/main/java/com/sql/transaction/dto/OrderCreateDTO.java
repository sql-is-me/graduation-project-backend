package com.sql.transaction.dto;

import lombok.Data;

@Data
public class OrderCreateDTO {

    /**
     * 商品类型：0-单课时购买  1-套餐购买
     * 不填默认为 0
     */
    private String productType = "0";

    /**
     * 购买课时数（productType=0 时必填，最少1课时）
     * productType=1 时忽略此字段，课时数由套餐决定
     */
    private Integer quantity;

    /**
     * 套餐类型（productType=1 时必填）
     * 见 PackageType 常量：10/30/50课时套餐
     */
    private String packageType;

    /**
     * 使用的用户优惠券ID（可选）
     */
    private Long userCouponId;
}
