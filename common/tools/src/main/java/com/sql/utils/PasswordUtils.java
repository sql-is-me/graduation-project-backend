package com.sql.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
    public final static String PWD_ERR_CNT_KEY = "pwd_err_ctr:";

    public final static Integer PASSWORD_MAX_RETRY_COUNT = 5;

    public final static Long PASSWORD_LOCK_TIME = 5L;

    private final static BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 登录账户密码错误次数缓存键名
     *
     */
    public static String getWrongPWTimesKey(String username) {
        return PWD_ERR_CNT_KEY + username;
    }

    /**
     * 生成BCryptPasswordEncoder密码
     */
    public static String encryptPassword(String password) {
        return ENCODER.encode(password);
    }

    /**
     * 判断新旧密码明文是否相同
     */
    public static boolean isEqualPassword(String newPassword, String oldPassword) {
        return newPassword.equals(oldPassword);
    }

    /**
     * 登录密码加密后比对数据库
     */
    public static boolean matchesPassword(String rawPassword, String encryptedPassword) {
        return ENCODER.matches(rawPassword, encryptedPassword);
    }
}
