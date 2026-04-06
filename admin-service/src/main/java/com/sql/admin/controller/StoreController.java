package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.StoreService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.dto.StoreCreateDTO;
import com.sql.common.entity.dto.StoreUpdateDTO;
import com.sql.common.entity.po.Store;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.CoachesInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.entity.vo.VIPsInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

@RestController
@RequestMapping("/admin/store")
@LoginRequired
public class StoreController extends BaseController {

    @Autowired
    private StoreService storeService;

    /**
     * 创建店铺
     */
    @RequiresType(UserTypes.ADMIN)
    @Log(title = "店铺管理", businessType = BusinessType.INSERT, operatorType = UserTypes.ADMIN)
    @PostMapping("/create")
    public R<?> createStore(@Validated @RequestBody StoreCreateDTO dto) {
        Long storeId = storeService.createStore(dto);
        return R.ok(storeId, "店铺创建成功");
    }

    /**
     * 修改店铺信息
     * 
     * 可修改店铺名和地址
     */
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    @Log(title = "店铺管理", businessType = BusinessType.UPDATE)
    @PutMapping("/update/{storeId}")
    public R<?> updateStore(@PathVariable Long storeId, @RequestBody StoreUpdateDTO dto) {
        storeService.updateStore(storeId, dto);
        return R.ok("店铺信息修改成功");
    }

    /**
     * 注销店铺
     */
    @RequiresType(UserTypes.ADMIN)
    @Log(title = "店铺管理", businessType = BusinessType.DELETE, operatorType = UserTypes.ADMIN)
    @DeleteMapping("/{storeId}")
    public R<?> deleteStore(@PathVariable Long storeId) {
        storeService.deleteStore(storeId);
        return R.ok("店铺注销成功");
    }

    /**
     * 设置店铺所有人
     */
    @RequiresType(UserTypes.ADMIN)
    @Log(title = "店铺管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.ADMIN)
    @PutMapping("/setOwner/{storeId}/{ownerId}")
    public R<?> setOwner(@PathVariable Long storeId, @PathVariable Long ownerId) {
        storeService.setOwner(storeId, ownerId);
        return R.ok("店铺所有人设置成功");
    }

    /**
     * 查询店铺列表
     * （默认不传参查询全部店铺）
     */
    @RequiresType(UserTypes.ADMIN)
    @GetMapping("/list")
    public TableDataInfo listStores(@RequestParam(required = false) String status) {
        startPage();
        List<Store> list = storeService.listStores(status);
        return getDataTable(list);
    }

    /**
     * 查询店铺详情
     */
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    @GetMapping("/{storeId}")
    public R<?> getStoreById(@PathVariable Long storeId) {
        return R.ok(storeService.getStoreById(storeId));
    }

    /**
     * 查看当前店铺旗下会员信息
     * 包含基础信息、孩子信息及课时余额
     */
    @RequiresType(UserTypes.MANAGER)
    @GetMapping("/list/vip")
    public TableDataInfo listStoreVIPs() {
        startPage();
        List<VIPsInfo> list = storeService.listStoreVIPs();
        return getDataTable(list);
    }

    /**
     * 查看当前店铺旗下教练信息
     */
    @RequiresType(UserTypes.MANAGER)
    @GetMapping("/list/coach")
    public TableDataInfo listStoreCoachs() {
        startPage();
        List<CoachesInfo> list = storeService.listStoreCoachs();
        return getDataTable(list);
    }
}
