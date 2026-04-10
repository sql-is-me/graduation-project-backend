package com.sql.common.enums;

public enum UserTypes {
    /**
     * 系统管理员
     */
    ADMIN("0"),

    /**
     * 店铺管理员
     */
    MANAGER("1"),

    /**
     * 教练
     */
    COACH("2"),

    /**
     * 会员
     */
    VIP("3"),

    /**
     * 未知
     */
    UNKNOWN("4");

    private final String code;

    UserTypes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * 管理员类型（code="0"时返回ADMIN，code="1"时返回MANAGER）
     */
    public static UserTypes adminFromCode(String code) {
        if (code == null) {
            return null;
        }
        if ("0".equals(code)) {
            return ADMIN;
        }
        if ("1".equals(code)) {
            return MANAGER;
        }
        return null;
    }

    /**
     * 用户类型（code="0"返回VIP会员，code="1"返回教练）
     */
    public static UserTypes userFromCode(String code) {
        if (code == null) {
            return null;
        }
        if ("0".equals(code)) {
            return VIP;
        }
        if ("1".equals(code)) {
            return COACH;
        }
        return null; // 其他code不返回
    }
}
