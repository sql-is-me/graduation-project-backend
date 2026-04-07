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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.service.CourtService;
import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.dto.CourtCreateDTO;
import com.sql.common.entity.dto.CourtUpdateDTO;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.CourtInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

@RestController
@RequestMapping("/admin/court")
@LoginRequired
public class CourtController extends BaseController {
    @Autowired
    private CourtService courtService;

    /**
     * 添加场地
     * 
     * @return courtId
     */

    @RequiresType(UserTypes.MANAGER)
    @Log(title = "场地管理", businessType = BusinessType.INSERT, operatorType = UserTypes.MANAGER)
    @PostMapping
    public R<?> addCourt(@Validated @RequestBody CourtCreateDTO dto) {
        return R.ok(courtService.addCourt(dto), "场地添加成功");
    }

    /**
     * 修改场地信息
     */

    @RequiresType(UserTypes.MANAGER)
    @Log(title = "场地管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.MANAGER)
    @PutMapping("/{courtId}")
    public R<?> updateCourt(@PathVariable Long courtId, @RequestBody CourtUpdateDTO dto) {
        courtService.updateCourt(courtId, dto);
        return R.ok("场地信息修改成功");
    }

    /**
     * 删除场地
     */

    @RequiresType(UserTypes.MANAGER)
    @Log(title = "场地管理", businessType = BusinessType.DELETE, operatorType = UserTypes.MANAGER)
    @DeleteMapping("/{courtId}")
    public R<?> deleteCourt(@PathVariable Long courtId) {
        courtService.deleteCourt(courtId);
        return R.ok("删除场地成功");
    }

    /**
     * 查询指定店铺的场地列表
     */
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    @GetMapping("/list")
    public TableDataInfo listCourts(@RequestParam(required = false) Long storeId) {
        startPage();
        List<CourtInfo> list = courtService.listCourts(storeId);
        return getDataTable(list);
    }

    /**
     * 查询场地详情
     */
    @RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
    @GetMapping("/{courtId}")
    public R<?> getCourtById(@PathVariable Long courtId) {
        return R.ok(courtService.getCourtById(courtId));
    }
}
