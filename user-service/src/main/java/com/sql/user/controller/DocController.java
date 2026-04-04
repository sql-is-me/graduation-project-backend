package com.sql.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.TeachingPlan;
import com.sql.common.entity.po.TrainingMethod;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.MyTeachingPlanInfo;
import com.sql.common.entity.vo.MyTrainingMethodInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.entity.vo.TrainingMethodInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.dto.TeachingPlanUploadDTO;
import com.sql.user.dto.TrainingMethodUploadDTO;
import com.sql.user.service.DocService;
import com.sql.utils.BaseController;

/**
 * 教练文档控制器（教案 + 训练方法）
 */
@RestController
@RequestMapping("/user/doc")
@LoginRequired
@RequiresType(UserTypes.COACH)
public class DocController extends BaseController {

    @Autowired
    private DocService docService;

    /**
     * 上传教案（文件 + 标题/描述一起提交）
     * 上传后状态为待审核，同时创建审核请求发给店铺管理员
     */
    @Log(title = "上传教案", businessType = BusinessType.INSERT)
    @PostMapping(value = "/tp/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<?> uploadTeachingPlan(
            @Validated @RequestPart("dto") TeachingPlanUploadDTO dto,
            @RequestPart("file") MultipartFile file) {
        docService.uploadTeachingPlan(dto, file);
        return R.ok("教案上传成功，等待店铺管理员审核");
    }

    /**
     * 查询我的教案列表
     */
    @GetMapping("/tp/list")
    public TableDataInfo listMyTeachingPlans() {
        startPage();
        List<MyTeachingPlanInfo> list = docService.listMyTeachingPlans();
        return getDataTable(list);
    }

    /**
     * 获取教案在线阅读 URL
     *
     * uniapp 端拿到 url 后通过 web-view 组件加载即可在线阅读
     * PDF 直接渲染；doc/docx 建议套一层 WPS/Office Online 在线预览地址
     * 仅限教案归属本人
     */
    @GetMapping("/teachingPlan/{tpId}/url")
    public R<?> getTeachingPlanUrl(@PathVariable Long tpId) {// TODO:路径是数据库中存储的相对路径，该接口可以接收相对路径并返回绝对路径
        String url = docService.getTeachingPlanFileUrl(tpId);
        return R.ok(url);
    }

    /**
     * 上传训练方法（文件 + 标题/描述一起提交）
     * 上传后状态为待审核，同时创建审核请求发给店铺管理员
     */
    @Log(title = "上传训练方法", businessType = BusinessType.INSERT)
    @PostMapping(value = "/tm/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<?> uploadTrainingMethod(
            @Validated @RequestPart("dto") TrainingMethodUploadDTO dto,
            @RequestPart("file") MultipartFile file) {
        docService.uploadTrainingMethod(dto, file);
        return R.ok("训练方法上传成功，等待店铺管理员审核");
    }

    /**
     * 查询本店铺已审核通过的所有训练方法（同店铺教练均可见）
     */
    @GetMapping("/tm/store")
    public TableDataInfo listStoreTrainingMethods() {
        startPage();
        List<TrainingMethodInfo> list = docService.listStoreTrainingMethods();// FIXME:JOIN查询返回训练方法列表时，顺带返回上传者姓名等信息，减少请求次数
        return getDataTable(list);
    }

    /**
     * 查询我上传的所有训练方法
     */
    @GetMapping("/tm/my")
    public TableDataInfo listMyTrainingMethods() {
        startPage();
        List<MyTrainingMethodInfo> list = docService.listMyTrainingMethods();
        return getDataTable(list);
    }

    /**
     * 获取训练方法在线阅读 URL
     *
     * 同店铺教练均可访问已审核通过的训练方法
     * uniapp 端通过 web-view 组件加载
     */
    @GetMapping("/trainingMethod/{tmId}/url")
    public R<?> getTrainingMethodUrl(@PathVariable Long tmId) {// TODO:路径是数据库中存储的相对路径，该接口可以接收相对路径并返回绝对路径
        String url = docService.getTrainingMethodFileUrl(tmId);
        return R.ok(url);
    }
}
