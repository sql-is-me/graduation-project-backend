package com.sql.common.entity.bo;

import lombok.Data;

/**
 * 教练简要信息
 */
@Data
public class CoachBriefInfo {
    /** 教练昵称 */
    private String nickName;

    /** 性别（0男 1女 2未知） */
    private String sex;

    /** 头像相对路径 */
    private String avatar;
}
