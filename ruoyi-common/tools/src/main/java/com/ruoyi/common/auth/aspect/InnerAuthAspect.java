package com.ruoyi.common.auth.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.ruoyi.common.ServletUtils;
import com.ruoyi.common.StringUtils;
import com.ruoyi.common.Constants.AuthConstants;
import com.ruoyi.common.Constants.ContextHolderConstants;
import com.ruoyi.common.auth.annotation.InnerAuth;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.exception.InnerAuthException;

/**
 * 内部服务调用验证切面
 *
 * 工作原理:
 *   1. 网关(AuthFilter)会清除外部请求中的 from-source 请求头
 *   2. FeignRequestInterceptor 在内部调用时自动添加 from-source = "inner"
 *   3. 本切面检查 from-source 请求头是否为 "inner"，不是则拒绝访问
 *
 * 使用方式:
 *   在 Controller 方法上添加 @InnerAuth 注解
 */
@Aspect
@Component
public class InnerAuthAspect implements Ordered {

    @Around("@annotation(innerAuth)")
    public Object innerAround(ProceedingJoinPoint point, InnerAuth innerAuth) throws Throwable {
        String source = ServletUtils.getRequest().getHeader(SecurityConstants.FROM_SOURCE);
        // 内部请求验证：请求头必须包含 from-source = "inner"
        if (!StringUtils.equals(AuthConstants.INNER, source)) {
            throw new InnerAuthException("没有内部访问权限，不允许访问");
        }

        // 用户信息验证（可选）
        if (innerAuth.isUser()) {
            String userId = ServletUtils.getRequest().getHeader(ContextHolderConstants.CH_ID);
            String username = ServletUtils.getRequest().getHeader(ContextHolderConstants.CH_USERNAME);
            if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(username)) {
                throw new InnerAuthException("没有设置用户信息，不允许访问");
            }
        }

        return point.proceed();
    }

    /**
     * 确保在权限认证AOP执行前执行（优先级最高）
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
