package com.ruoyi.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.admin.dto.AdminPasswordUpdateDTO;
import com.ruoyi.admin.dto.AdminInfoUpdateDTO;
import com.ruoyi.admin.service.Impl.InfoServiceImpl;
import com.ruoyi.common.auth.annotation.RequiresType;
import com.ruoyi.common.entity.Admin;
import com.ruoyi.common.entity.result.R;
import com.ruoyi.common.enums.UserTypes;

/**
 * 管理员个人信息控制器
 * 顶级管理员和地区管理员均可访问
 */
@RestController
@RequestMapping("/admin/info")
@RequiresType({UserTypes.ADMIN, UserTypes.MANAGER})
public class InfoController {

    @Autowired
    private InfoServiceImpl infoService;

    /**
     * 获取管理员个人信息
     */
    @GetMapping
    public R<?> getInfo() {
        Admin admin = infoService.getInfo();
        return R.ok(admin);
    }

    /**
     * 修改管理员个人信息
     */
    @PutMapping("/updateInfo")
    public R<?> updateInfo(@RequestBody AdminInfoUpdateDTO dto) {
        infoService.updateInfo(dto);
        return R.ok("个人信息修改成功");
    }

    /**
     * 修改管理员密码
     */
    @PutMapping("/updatePassword")
    public R<?> updatePassword(@RequestBody AdminPasswordUpdateDTO dto) {
        infoService.updatePassword(dto);
        return R.ok("密码修改成功");
    }

    /**
     * 管理员头像上传
     */
    @PutMapping("/updateAvatar")
    public R<?> updateAvatar(@RequestParam("avatarFile") MultipartFile mf) {
        infoService.updateAvatar(mf);

        return R.ok("头像更新成功");
    }
}
