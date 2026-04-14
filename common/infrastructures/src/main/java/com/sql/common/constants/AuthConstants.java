package com.sql.common.constants;

public class AuthConstants {
    /**
     * 授权信息字段
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 请求来源字段
     */
    public static final String FROM_SOURCE = "from-source";

    /**
     * 内部请求
     */
    public static final String INNER = "inner";

    /**
     * 登录成功状态
     */
    public static final String LOGIN_SUCCESS_STATUS = "0";

    /**
     * 登录失败状态
     */
    public static final String LOGIN_FAIL_STATUS = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 管理员邀请码缓存前缀
     */
    public static final String INVITE_CODE = "inviteCode:";

    /**
     * 管理员邀请码反查前缀（防重复生成）
     * key: inviteAdmin:{referrerId}:{storeId}
     */
    public static final String INVITE_ADMIN = "inviteAdmin:";

    /**
     * 管理员邀请码过期时间 30min
     */
    public static final Long ADMIN_INVITE_EXPIRE = 30L;

    /**
     * 教练邀请码缓存前缀
     */
    public static final String INVITE_COACH_CODE = "inviteCoachCode:";

    /**
     * 教练邀请码反查前缀（防重复生成）
     * key: inviteCoach:{adminId}:{storeId}
     */
    public static final String INVITE_COACH = "inviteCoach:";

    /**
     * 教练邀请码过期时间 30min
     */
    public static final Long COACH_INVITE_EXPIRE = 30L;

    /**
     * 绑定店铺邀请码缓存前缀
     */
    public static final String BIND_STORE_CODE = "bindStoreCode:";

    /**
     * 绑定店铺邀请码反查前缀（防重复生成）
     * key: bindStore:{adminId}:{storeId}
     */
    public static final String BIND_STORE = "bindStore:";

    /**
     * 绑定店铺邀请码过期时间 30min
     */
    public static final Long BIND_STORE_EXPIRE = 30L;

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;

    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 5;

    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 网关验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 5;
}
