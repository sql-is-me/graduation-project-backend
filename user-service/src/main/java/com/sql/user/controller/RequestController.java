package com.sql.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.Request;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.dto.BindStoreRequestDTO;
import com.sql.user.dto.LeaveRequestDTO;
import com.sql.user.service.RequestService;
import com.sql.utils.BaseController;

/**
 * 审批请求控制器（用户/教练端 —— 发起请求）
 */
@RestController
@RequestMapping("/user/request")
@LoginRequired
public class RequestController extends BaseController {

    @Autowired
    private RequestService requestService;

    // ─────────────── 会员请假 ───────────────

    /**
     * 会员发起请假申请
     */
    @RequiresType(UserTypes.VIP)
    @Log(title = "请假申请", businessType = BusinessType.INSERT)
    @PostMapping("/leave")
    public R<?> submitLeave(@Validated @RequestBody LeaveRequestDTO dto) {
        requestService.submitLeave(dto.getCourseId(), dto.getChildId(), dto.getMessage());
        return R.ok("请假申请已提交，等待店铺管理员处理");
    }

    // ─────────────── 绑定店铺 ───────────────

    /**
     * 通过邀请码申请绑定店铺（会员 / 教练均可）
     * 未绑定店铺时直接绑定；已绑定则提交审批
     */
    @RequiresType({ UserTypes.VIP, UserTypes.COACH })
    @Log(title = "绑定店铺申请", businessType = BusinessType.INSERT)
    @PostMapping("/bindStore")
    public R<?> submitBindStore(@Validated @RequestBody BindStoreRequestDTO dto) {
        requestService.submitBindStore(dto.getInviteCode(), dto.getMessage());
        return R.ok("操作成功");
    }

    /**
     * 查询我发起的所有请求
     */
    @RequiresType({ UserTypes.VIP, UserTypes.COACH })
    @GetMapping("/my")
    public TableDataInfo listMyRequests() {
        startPage();
        List<Request> list = requestService.listMyRequests();
        return getDataTable(list);
    }
}
