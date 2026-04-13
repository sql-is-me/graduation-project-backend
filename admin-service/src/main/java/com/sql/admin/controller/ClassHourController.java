package com.sql.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.admin.service.ClassHourService;
import com.sql.utils.BaseController;

/**
 * 课时管理接口
 */
@RestController
@RequestMapping("/admin/classHour")
@LoginRequired
public class ClassHourController extends BaseController {

    @Autowired
    private ClassHourService classHourService;

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
