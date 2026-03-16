package com.sql.common.auth.annotation;

import java.lang.annotation.*;

import com.sql.common.enums.UserTypes;

/**
 * 用户类型权限注解
 * 标记的接口仅允许指定类型的用户访问
 *
 * 用户类型体系:
 *   UserTypes.ADMIN   ("0") — 顶级管理员 (Admin.adminType = "0")
 *   UserTypes.MANAGER ("1") — 地区/店铺管理员 (Admin.adminType = "1")
 *   UserTypes.COACH   ("2") — 教练 (User.userType = "1")
 *   UserTypes.VIP     ("3") — 普通会员 (User.userType = "0")
 *
 * 使用示例:
 *   @RequiresType(UserTypes.ADMIN)                           — 仅顶级管理员
 *   @RequiresType({UserTypes.ADMIN, UserTypes.MANAGER})      — 顶级管理员或地区管理员
 *   @RequiresType(UserTypes.VIP)                             — 仅普通会员
 *   @RequiresType(UserTypes.COACH)                           — 仅教练
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresType {
    /**
     * 允许访问的用户类型列表（满足其一即可）
     */
    UserTypes[] value();
}
