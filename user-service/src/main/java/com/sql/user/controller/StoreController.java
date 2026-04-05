package com.sql.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.bo.StoreBriefInfo;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.StoreAndCoachInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.user.service.StoreService;
import com.sql.utils.BaseController;

/**
 * 店铺信息控制器（用户侧）
 * 教练和会员均可访问
 */
@RestController
@RequestMapping("/user/store")
@LoginRequired
@RequiresType({ UserTypes.COACH, UserTypes.VIP })
public class StoreController extends BaseController {

    @Autowired
    private StoreService storeService;

    /**
     * 查询所属店铺详情（含店铺管理员信息和教练列表）
     */
    @GetMapping("/info")
    public R<?> getStoreInfo() {
        StoreAndCoachInfo info = storeService.getStoreInfo(null);
        return R.ok(info);
    }

    /**
     * 查询店铺详情（含店铺管理员信息和教练列表）
     */
    @GetMapping("/info/{storeId}")
    public R<?> getStoreInfo(@PathVariable Long storeId) {
        StoreAndCoachInfo info = storeService.getStoreInfo(storeId);
        return R.ok(info);
    }

    /**
     * 模糊搜索店铺（按名称关键字），用于用户自行联系管理员索要邀请码
     */
    @RequiresType({ UserTypes.VIP, UserTypes.COACH })
    @GetMapping("/search")
    public TableDataInfo searchStores(@RequestParam String keyword) {
        startPage();
        List<StoreBriefInfo> list = storeService.searchStores(keyword);
        return getDataTable(list);
    }
}
