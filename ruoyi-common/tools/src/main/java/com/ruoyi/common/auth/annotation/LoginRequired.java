package com.ruoyi.common.auth.annotation;

import java.lang.annotation.*;

/**
 * 登录认证注解
 * 标记的接口需要用户已登录才能访问，不限制用户类型
 *
 * 使用示例:
 *   @LoginRequired — 需要登录才能访问
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequired {
}
