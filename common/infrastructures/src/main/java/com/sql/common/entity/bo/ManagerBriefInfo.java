package com.sql.common.entity.bo;

import lombok.Data;

/**
 * 店铺管理员简要信息（mapper 查询的中间结果）
 */
@Data
public class ManagerBriefInfo {
    /** 管理员昵称 */
    private String nickName;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 性别（0男 1女 2未知） */
    private String sex;

    /** 头像相对路径 */
    private String avatar;
}
