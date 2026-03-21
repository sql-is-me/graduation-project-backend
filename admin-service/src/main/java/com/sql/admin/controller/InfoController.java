package com.sql.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.admin.dto.AdminPasswordUpdateDTO;
import com.sql.admin.dto.AdminUpdateEmailDTO;
import com.sql.admin.dto.AdminInfoUpdateDTO;
import com.sql.admin.service.InfoService;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.common.vo.AdminInfo;

/**
 * 管理员个人信息控制器
 * 顶级管理员和地区管理员均可访问
 */
@RestController
@RequestMapping("/admin/info")
@RequiresType({ UserTypes.ADMIN, UserTypes.MANAGER })
public class InfoController {

    @Autowired
    private InfoService infoService;

    /**
     * 获取管理员个人信息
     */
    @GetMapping
    public R<?> getInfo() {
        AdminInfo adminInfo = infoService.getInfo();
        return R.ok(adminInfo);
    }

    /**
     * 修改管理员个人信息
     * 
     * nickName、phone、sex
     */
    @Log(title = "管理员个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateInfo")
    public R<?> updateInfo(@Validated @RequestBody AdminInfoUpdateDTO dto) {
        infoService.updateInfo(dto);
        return R.ok("个人信息修改成功");
    }

    /**
     * 修改管理员邮箱
     */
    @Log(title = "管理员邮箱", businessType = BusinessType.UPDATE)
    @PutMapping("/updateEmail")
    public R<?> updateAdminEmail(@Validated @RequestBody AdminUpdateEmailDTO dto) {
        infoService.updateAdminEmail(dto);
        return R.ok("邮箱更新成功");
    }

    /**
     * 修改管理员密码
     */
    @Log(title = "管理员密码", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePassword")
    public R<?> updatePassword(@Validated @RequestBody AdminPasswordUpdateDTO dto) {
        infoService.updatePassword(dto);
        return R.ok("密码修改成功");
    }

    /**
     * 管理员头像上传
     */
    @Log(title = "管理员头像", businessType = BusinessType.UPDATE)
    @PutMapping("/updateAvatar")
    public R<?> updateAvatar(@RequestParam("avatarFile") MultipartFile mf) {
        infoService.updateAvatar(mf);

        return R.ok("头像更新成功");
    }
}
