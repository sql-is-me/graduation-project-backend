package com.sql.transaction.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.transaction.service.ClassHourService;
import com.sql.utils.BaseController;

/**
 * 课时管理接口
 */
@RestController
@RequestMapping("/transaction/classHour")
public class ClassHourController extends BaseController {

    @Autowired
    private ClassHourService classHourService;

    /**
     * 增加用户课时
     */
    @Log(title = "课时管理", businessType = BusinessType.UPDATE, operatorType = UserTypes.ADMIN)
    @RequiresType(UserTypes.ADMIN)
    @PostMapping("/add")
    public R<Boolean> addClassHours(@RequestParam("userId") Long userId,
            @RequestParam("hours") int hours) {
        int rows = classHourService.addClassHours(userId, hours);
        if (rows > 0) {
            return R.ok(true);
        } else {
            return R.fail("增加课时失败");
        }
    }

    /**
     * 查看当前店铺旗下会员的课时余额
     */
    @GetMapping("/list")
    @RequiresType(UserTypes.MANAGER)
    public TableDataInfo listClassHours() {
        startPage();
        List<ClassHour> list = classHourService.listClassHours();
        return getDataTable(list);
    }
}
