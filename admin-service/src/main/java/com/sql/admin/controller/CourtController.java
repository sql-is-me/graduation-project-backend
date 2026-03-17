package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.admin.dto.CourtCreateDTO;
import com.sql.admin.dto.CourtUpdateDTO;
import com.sql.admin.service.CourtService;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.TableDataInfo;
import com.sql.common.entity.db.Court;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.utils.BaseController;

@RestController
@RequestMapping("/admin/court")
@RequiresType(UserTypes.MANAGER)
public class CourtController extends BaseController {

    @Autowired
    private CourtService courtService;

    /**
     * 添加场地
     */
    @Log(title = "场地管理", businessType = BusinessType.INSERT)
    @PostMapping("/add/{courtId}")
    public R<?> addCourt(@Validated @PathVariable Long courtId, @RequestBody CourtCreateDTO dto) {
        return R.ok(courtService.addCourt(dto), "场地添加成功");
    }

    /**
     * 修改场地信息
     */
    @Log(title = "场地管理", businessType = BusinessType.UPDATE)
    @PutMapping("/update")
    public R<?> updateCourt(@Validated @RequestBody CourtUpdateDTO dto) {
        return R.ok(courtService.updateCourt(dto), "场地信息修改成功");
    }

    /**
     * 查询当前店铺的场地列表
     */
    @GetMapping("/list")
    public TableDataInfo listCourts() {
        startPage();
        List<Court> list = courtService.listCourts();
        return getDataTable(list);
    }

    /**
     * 查询场地详情
     */
    @GetMapping("/{courtId}")
    public R<?> getCourtById(@PathVariable Long courtId) {
        return R.ok(courtService.getCourtById(courtId));
    }
}
