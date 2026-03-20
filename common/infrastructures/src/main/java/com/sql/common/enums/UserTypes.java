package com.sql.common.enums;

public enum UserTypes {
    /**
     * 顶级管理员
     */
    ADMIN("0"),

    /**
     * 地区管理员
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

    public static UserTypes fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserTypes type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
