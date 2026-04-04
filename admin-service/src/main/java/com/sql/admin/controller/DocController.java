package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.DocService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.TeachingPlan;
import com.sql.common.entity.po.TrainingMethod;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.entity.vo.TeachingPlanInfo;
import com.sql.common.entity.vo.TrainingMethodInfo;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.utils.BaseController;

/**
 * 店铺文档控制器（教案 + 训练方法）
 * 仅店铺管理员可访问，只能查看本店铺的文档
 */
@RestController
@RequestMapping("/admin/doc")
@LoginRequired
@RequiresType(UserTypes.MANAGER)
public class DocController extends BaseController {

    @Autowired
    private DocService docService;

    // ─────────────── 教案 ───────────────

    /**
     * 查询本店铺所有教案列表（含各审核状态）
     */
    @GetMapping("/tp/list")
    public TableDataInfo listTeachingPlans() {
        startPage();
        List<TeachingPlanInfo> list = docService.listTeachingPlans();
        return getDataTable(list);
    }

    /**
     * 查询教案详情
     */
    @GetMapping("/tp/{tpId}")
    public R<?> getTeachingPlan(@PathVariable Long tpId) {
        TeachingPlan tp = docService.getTeachingPlan(tpId);
        return R.ok(tp);
    }

    /**
     * 获取教案在线阅读 URL（Web 端直接在新标签打开或嵌入 iframe）
     *
     * 返回 file-service 的完整可访问地址，前端用该地址直接访问文件
     * PDF 可直接由浏览器渲染；doc/docx 建议前端通过 Office Online / WPS 在线预览接口封装
     */
    @GetMapping("/teachingPlan/{tpId}/url")
    public R<?> getTeachingPlanUrl(@PathVariable Long tpId) { // TODO:路径是数据库中存储的相对路径，该接口可以接收相对路径并返回绝对路径
        String url = docService.getTeachingPlanFileUrl(tpId);
        return R.ok(url);
    }

    // ─────────────── 训练方法 ───────────────

    /**
     * 查询本店铺所有训练方法列表（含各审核状态）
     */
    @GetMapping("/tm/list")
    public TableDataInfo listTrainingMethods() {
        startPage();
        List<TrainingMethodInfo> list = docService.listTrainingMethods();
        return getDataTable(list);
    }

    /**
     * 查询训练方法详情
     */
    @GetMapping("/tm/{tmId}")
    public R<?> getTrainingMethod(@PathVariable Long tmId) {
        TrainingMethod tm = docService.getTrainingMethod(tmId);
        return R.ok(tm);
    }

    /**
     * 获取训练方法在线阅读 URL（Web 端直接在新标签打开或嵌入 iframe）
     */
    @GetMapping("/tm/{tmId}/url")
    public R<?> getTrainingMethodUrl(@PathVariable Long tmId) {// TODO:路径是数据库中存储的相对路径，该接口可以接收相对路径并返回绝对路径
        String url = docService.getTrainingMethodFileUrl(tmId);
        return R.ok(url);
    }
}
