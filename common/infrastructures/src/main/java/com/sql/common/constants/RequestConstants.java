package com.sql.common.constants;

public class RequestConstants {

    // ── 教练发起 ──

    /** 教练申请上传教案（审核人：所属店铺管理员） */
    public static final String COACH_UPLOAD_TEACHING_PLAN = "coach_upload_teaching_plan";

    /** 教练申请上传训练方法（审核人：所属店铺管理员） */
    public static final String COACH_UPLOAD_TRAINING_METHOD = "coach_upload_training_method";

    /** 教练申请更换绑定店铺（审核人：原店铺管理员 + 目标店铺管理员 + 系统管理员） */
    public static final String COACH_CHANGE_STORE = "coach_change_store";

    // ── 会员发起 ──

    /** 会员申请请假（审核人：所属店铺管理员） */
    public static final String VIP_LEAVE = "vip_leave";

    /** 会员申请更换绑定店铺（审核人：原店铺管理员 + 目标店铺管理员 + 系统管理员） */
    public static final String VIP_CHANGE_STORE = "vip_change_store";

    // ── 审核人状态 ──

    public static final String APPROVER_PENDING = "0";
    public static final String APPROVER_APPROVED = "1";
    public static final String APPROVER_REJECTED = "2";

    // ── 整体状态 ──

    public static final String STATUS_PENDING = "0";
    public static final String STATUS_APPROVED = "1";
    public static final String STATUS_REJECTED = "2";
}
