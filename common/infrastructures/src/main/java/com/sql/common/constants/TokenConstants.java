package com.sql.common.constants;

/**
 * Token的Key常量
 */
public class TokenConstants {
    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥
     */
    public final static String SECRET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 管理员token缓存的前缀
     */
    public final static String ADMIN_TOKENS = "admin_tokens:";

    /**
     * 用户token缓存前缀
     */
    public final static String USER_TOKENS = "user_tokens:";

}
