package com.sql.common.constants;

/**
 * 缓存常量信息
 */
public class CacheConstants {
    /**
     * 缓存有效期，默认360（分钟）
     */
    public final static Long TOKEN_EXPIRE_TIME = 360L;

    /**
     * 缓存刷新时间，默认60（分钟）
     */
    public final static Long REFRESH_TIME = 60L;

    /**
     * 分钟转毫秒
     */
    public final static Long MILLIS_MINUTE = 60000L;
}
