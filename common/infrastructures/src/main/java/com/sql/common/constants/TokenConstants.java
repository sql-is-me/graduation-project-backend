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
     * 管理员ID到token的反向映射前缀
     */
    public final static String ADMIN_TOKEN_MAPPING = "admin_token_mapping:";

    /**
     * 用户session_key缓存前缀
     */
    public final static String USER_SESSION_KEYS = "user_session_keys:";

    /**
     * 用户ID到session_key的反向映射前缀
     */
    public final static String USER_SESSION_KEY_MAPPING = "user_session_key_mapping:";

    /**
     * 微信 access_token 缓存 key
     */
    public final static String WX_ACCESS_TOKEN = "wx:access_token";

}
