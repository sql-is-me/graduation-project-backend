package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.RequestService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.Request;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

/**
 * 审批控制器（管理员端）
 * 店铺管理员：处理本店铺的待审请求
 * 系统管理员：处理所有需要系统管理员审批的请求
 */
@RestController
@RequestMapping("/admin/request")
@LoginRequired
@RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
public class RequestController extends BaseController {

    @Autowired
    private RequestService requestService;

    /**
     * 查询待审批请求列表
     * 店铺管理员：返回本店铺所有待审请求
     * 系统管理员：返回所有待系统管理员审批的请求
     */
    @GetMapping("/pending")
    public TableDataInfo listPendingRequests() {
        startPage();
        List<Request> list = requestService.listPendingRequests();
        return getDataTable(list);
    }

    /**
     * 审批通过
     */
    @Log(title = "审批请求", businessType = BusinessType.UPDATE)
    @PostMapping("/{requestId}/approve")
    public R<?> approve(@PathVariable Long requestId) {
        requestService.approve(requestId);
        return R.ok("审批通过");
    }

    /**
     * 审批拒绝（会员请假不可拒绝）
     */
    @Log(title = "审批请求", businessType = BusinessType.UPDATE)
    @PostMapping("/{requestId}/reject")
    public R<?> reject(@PathVariable Long requestId,
            @RequestParam(required = false) String rejectReason) {
        requestService.reject(requestId, rejectReason);
        return R.ok("已拒绝");
    }
}
