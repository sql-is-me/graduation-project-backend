package com.ruoyi.common.auth.annotation;

import java.lang.annotation.*;

/**
 * 内部认证注解
 * 标记的接口仅允许内部服务间（Feign）调用，外部请求将被拒绝
 *
 * 使用示例:
 *   @InnerAuth                     — 仅内部调用
 *   @InnerAuth(isUser = true)      — 仅内部调用，且必须携带用户信息
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InnerAuth {
    /**
     * 是否校验用户信息（请求头中必须包含用户ID和用户名）
     */
    boolean isUser() default false;
}
