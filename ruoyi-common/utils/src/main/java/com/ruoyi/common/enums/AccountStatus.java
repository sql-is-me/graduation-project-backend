package com.ruoyi.common.enums;

/**
 * 用户状态
 */
public enum AccountStatus {
    OK("0"), DISABLE("1");

    private final String code;

    AccountStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
