package com.sql.common.constants;

public class JWTConstants {
    /**
     * JWT存取token字段
     */
    public static final String DETAILS_TOKEN = "token";

    /**
     * JWT存取session_key字段
     */
    public static final String DETAILS_SESSION_KEY = "session_key";

    /**
     * JWT存取ID字段
     */
    public static final String DETAILS_ID = "id";

    /**
     * JWT存取用户名字段
     * DETAILS_TYPE为0时，该值代表username
     * DETAILS_TYPE为1时，该值代表openId
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * JWT存取用户类型字段
     * 0:系统管理员/店铺管理员
     * 1:会员/教练
     */
    public static final String DETAILS_TYPE = "type";
}
