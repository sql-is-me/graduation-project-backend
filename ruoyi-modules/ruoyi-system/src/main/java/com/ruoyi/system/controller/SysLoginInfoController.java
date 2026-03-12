package com.ruoyi.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.system.api.domain.SysLogininfor;
import com.ruoyi.system.service.ISysLogininforService;

/**
 * 系统访问记录
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/loginInfo")
public class SysLoginInfoController extends BaseController {
    @Autowired
    private ISysLogininforService loginInfoService;

    @InnerAuth
    @PostMapping
    public AjaxResult add(@RequestBody SysLogininfor loginInfo) {
        return toAjax(loginInfoService.insertLogininfor(loginInfo));
    }
}
