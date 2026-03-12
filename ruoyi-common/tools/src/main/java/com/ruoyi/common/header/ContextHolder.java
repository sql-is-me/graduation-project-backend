package com.ruoyi.common.header;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.ruoyi.common.Convert;
import com.ruoyi.common.StringUtils;
import com.ruoyi.common.Constants.ContextHolderConstants;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.entity.UserOnline;

/**
 * 获取当前线程变量中的 用户id、用户名称、Token等信息
 * 注意： 必须在网关通过请求头的方法传入，同时在HeaderInterceptor拦截器设置值。 否则这里无法获取
 *
 * @author ruoyi
 */
public class ContextHolder {
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? StringUtils.EMPTY : value);
    }

    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        return Convert.toStr(map.getOrDefault(key, StringUtils.EMPTY));
    }

    public static <T> T get(String key, Class<T> clazz) {
        Map<String, Object> map = getLocalMap();
        return StringUtils.cast(map.getOrDefault(key, null));
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static Long getId() {
        return Convert.toLong(get(ContextHolderConstants.CH_ID));
    }

    public static void setId(String id) {
        set(ContextHolderConstants.CH_ID, id);
    }

    public static String getUsername() {
        return get(ContextHolderConstants.CH_USERNAME);
    }

    public static void setUsername(String username) {
        set(ContextHolderConstants.CH_USERNAME, username);
    }

    public static String getToken() {
        return get(ContextHolderConstants.CH_TOKEN);
    }

    public static void setToken(String token) {
        set(ContextHolderConstants.CH_TOKEN, token);
    }

    public static String getType() {
        return get(ContextHolderConstants.CH_TYPE);
    }

    public static void setType(String type) {
        set(ContextHolderConstants.CH_TYPE, type);
    }

    public static void setAO(AdminOnline ao) {
        set(ContextHolderConstants.CH_ADMIN_ONLINE, ao);
    }

    public static AdminOnline getAO() {
        return get(ContextHolderConstants.CH_ADMIN_ONLINE, AdminOnline.class);
    }

    // UserOnline
    public static void setUO(UserOnline uo) {
        set(ContextHolderConstants.CH_USER_ONLINE, uo);
    }

    public static UserOnline getUO() {
        return get(ContextHolderConstants.CH_USER_ONLINE, UserOnline.class);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
