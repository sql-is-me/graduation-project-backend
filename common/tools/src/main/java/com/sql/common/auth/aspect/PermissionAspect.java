package com.sql.common.auth.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.auth.util.AuthUtil;
import com.sql.common.enums.UserTypes;
import com.sql.common.exception.PreAuthorizeException;

/**
 * 权限验证切面
 *
 * 处理以下注解:
 * 
 * @LoginRequired — 仅需登录
 * @RequiresType — 需要指定用户类型
 *
 *               验证逻辑:
 *               1. 先检查方法级注解，再检查类级注解
 *               2. 方法级注解优先于类级注解（方法级存在时忽略类级）
 *               3. @RequiresType 内部隐含登录校验
 */
@Aspect
@Component
public class PermissionAspect implements Ordered {

    /**
     * 切入所有使用权限注解的方法
     */
    public static final String POINTCUT_SIGN = "@annotation(com.sql.common.auth.annotation.LoginRequired) || "
            + "@annotation(com.sql.common.auth.annotation.RequiresType) || "
            + "@within(com.sql.common.auth.annotation.LoginRequired) || "
            + "@within(com.sql.common.auth.annotation.RequiresType)";

    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        // 方法级 @RequiresType 优先
        RequiresType methodRequiresType = method.getAnnotation(RequiresType.class);
        if (methodRequiresType != null) {
            checkRequiresType(methodRequiresType);
            return joinPoint.proceed();
        }

        // 方法级 @LoginRequired
        LoginRequired methodLoginRequired = method.getAnnotation(LoginRequired.class);
        if (methodLoginRequired != null) {
            checkLogin();
            return joinPoint.proceed();
        }

        // 类级 @RequiresType
        RequiresType classRequiresType = targetClass.getAnnotation(RequiresType.class);
        if (classRequiresType != null) {
            checkRequiresType(classRequiresType);
            return joinPoint.proceed();
        }

        // 类级 @LoginRequired
        LoginRequired classLoginRequired = targetClass.getAnnotation(LoginRequired.class);
        if (classLoginRequired != null) {
            checkLogin();
            return joinPoint.proceed();
        }

        return joinPoint.proceed();
    }

    /**
     * 检查登录状态
     */
    private void checkLogin() {
        if (!AuthUtil.isLoggedIn()) {
            throw new PreAuthorizeException("用户未登录");
        }
    }

    /**
     * 检查用户类型权限
     */
    private void checkRequiresType(RequiresType requiresType) {
        // 隐含登录校验
        UserTypes currentType = AuthUtil.getCurrentUserType();
        if (currentType == null) {
            throw new PreAuthorizeException("用户权限不符");
        }

        UserTypes[] allowedTypes = requiresType.value();
        boolean matched = false;
        for (UserTypes allowed : allowedTypes) {
            if (allowed == currentType) {
                matched = true;
                break;
            }
        }

        if (!matched) {
            String allowedStr = Arrays.stream(allowedTypes)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new PermissionDeniedException(
                    "当前用户类型[" + currentType.name() + "]无权访问，需要类型: [" + allowedStr + "]");
        }
    }

    /**
     * 在 InnerAuthAspect 之后执行
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    /**
     * 权限不足异常（运行时异常，便于全局异常处理器捕获）
     */
    public static class PermissionDeniedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public PermissionDeniedException(String message) {
            super(message);
        }
    }
}
