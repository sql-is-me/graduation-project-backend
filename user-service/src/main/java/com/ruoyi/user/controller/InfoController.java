package com.ruoyi.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.entity.R;
import com.ruoyi.common.entity.User;
import com.ruoyi.user.dto.UserInfoUpdateDTO;
import com.ruoyi.user.dto.UserPasswordUpdateDTO;
import com.ruoyi.user.service.InfoService;

/**
 * 用户/教练个人信息控制器
 */
@RestController
@RequestMapping("/user/info")
public class InfoController {

    @Autowired
    private InfoService infoService;

    /**
     * 获取当前登录用户个人信息
     */
    @GetMapping
    public R<?> getInfo() {
        User user = infoService.getInfo();
        return R.ok(user);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/updateInfo")
    public R<?> updateInfo(@RequestBody UserInfoUpdateDTO dto) {
        infoService.updateInfo(dto);
        return R.ok("个人信息修改成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public R<?> updatePassword(@RequestBody UserPasswordUpdateDTO dto) {
        infoService.updatePassword(dto);
        return R.ok("密码修改成功");
    }

    /**
     * 头像上传
     */
    @PutMapping("/updateAvatar")
    public R<?> updateAvatar(@RequestParam("avatarFile") MultipartFile mf) {
        infoService.updateAvatar(mf);
        return R.ok("头像更新成功");
    }
}
