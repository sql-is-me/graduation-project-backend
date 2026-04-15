package com.sql.transaction.constants;

import java.math.BigDecimal;

/**
 * 套餐类型常量
 * 套餐以固定折扣价批量购买课时
 */
public class PackageType {

    /** 10课时套餐，原价500元，9折优惠 */
    public static final String P10 = "p10";
    public static final int P10_HOURS = 10;
    public static final BigDecimal P10_PRICE = new BigDecimal("450.00");

    /** 30课时套餐，原价1500元，8.5折优惠 */
    public static final String P30 = "p30";
    public static final int P30_HOURS = 30;
    public static final BigDecimal P30_PRICE = new BigDecimal("1275.00");

    /** 50课时套餐，原价2500元，8折优惠 */
    public static final String P50 = "p50";
    public static final int P50_HOURS = 50;
    public static final BigDecimal P50_PRICE = new BigDecimal("2000.00");

    private PackageType() {}
}
