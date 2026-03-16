package com.ruoyi.common.auth.util;

import com.ruoyi.common.entity.Admin;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.entity.User;
import com.ruoyi.common.entity.UserOnline;
import com.ruoyi.common.enums.UserTypes;
import com.ruoyi.common.header.ContextHolder;
import com.ruoyi.utils.StringUtils;

/**
 * 权限验证工具类
 * 从当前线程上下文中获取登录用户信息，判断用户类型
 *
 * 类型映射关系:
 * ContextHolder.type = "0" → 管理员端
 * AdminOnline.adminInfo.adminType = "0" → ADMIN（顶级管理员）
 * AdminOnline.adminInfo.adminType = "1" → MANAGER（地区管理员）
 * ContextHolder.type = "1" → 用户端
 * UserOnline.userInfo.userType = "0" → VIP（普通会员）
 * UserOnline.userInfo.userType = "1" → COACH（教练）
 */
public class AuthUtil {

    /**
     * 获取当前登录用户的统一类型枚举
     *
     * @return UserTypes枚举，未登录或类型无法识别返回null
     */
    public static UserTypes getCurrentUserType() {
        String type = ContextHolder.getType();

        if (StringUtils.isEmpty(type)) {
            return null;
        }

        if ("0".equals(type)) {
            // 管理员端
            AdminOnline ao = ContextHolder.getAO();
            if (ao == null || ao.getAdminInfo() == null) {
                return null;
            }
            Admin admin = ao.getAdminInfo();
            if (admin.isTopAdmin()) {
                return UserTypes.ADMIN;
            } else {
                return UserTypes.MANAGER;
            }
        } else if ("1".equals(type)) {
            // 用户端
            UserOnline uo = ContextHolder.getUO();
            if (uo == null || uo.getUserInfo() == null) {
                return null;
            }
            User user = uo.getUserInfo();
            if (user.isCoach()) {
                return UserTypes.COACH;
            } else {
                return UserTypes.VIP;
            }
        }

        return null;
    }

    /**
     * 判断当前用户是否已登录
     *
     * @return true=已登录
     */
    public static boolean isLoggedIn() {
        return getCurrentUserType() != null;
    }

    /**
     * 判断当前用户是否为指定类型
     *
     * @param userType 用户类型
     * @return true=是
     */
    public static boolean isType(UserTypes userType) {
        return userType == getCurrentUserType();
    }

    /**
     * 判断当前用户是否为指定类型中的任意一个
     *
     * @param userTypes 用户类型数组
     * @return true=匹配到任一类型
     */
    public static boolean isAnyType(UserTypes... userTypes) {
        UserTypes current = getCurrentUserType();
        if (current == null) {
            return false;
        }
        for (UserTypes ut : userTypes) {
            if (ut == current) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前用户是否为管理员（顶级或地区）
     *
     * @return true=管理员
     */
    public static boolean isAdmin() {
        return isAnyType(UserTypes.ADMIN, UserTypes.MANAGER);
    }

    /**
     * 判断当前用户是否为顶级管理员
     *
     * @return true=顶级管理员
     */
    public static boolean isTopAdmin() {
        return isType(UserTypes.ADMIN);
    }

    /**
     * 获取当前管理员在线信息（仅管理员端可用）
     *
     * @return AdminOnline，非管理员返回null
     */
    public static AdminOnline getAdminOnline() {
        if (!"0".equals(ContextHolder.getType())) {
            return null;
        }
        return ContextHolder.getAO();
    }

    /**
     * 获取当前用户在线信息（仅用户端可用）
     *
     * @return UserOnline，非用户返回null
     */
    public static UserOnline getUserOnline() {
        if (!"1".equals(ContextHolder.getType())) {
            return null;
        }
        return ContextHolder.getUO();
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        return ContextHolder.getId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        return ContextHolder.getUsername();
    }
}
