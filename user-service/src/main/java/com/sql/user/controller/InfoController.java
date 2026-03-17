package com.sql.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.db.User;
import com.sql.common.entity.result.R;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.dto.UserInfoUpdateDTO;
import com.sql.user.dto.UserPasswordUpdateDTO;
import com.sql.user.service.InfoService;

/**
 * 用户/教练个人信息控制器
 * 教练和会员均可访问
 */
@RestController
@RequestMapping("/user/info")
@RequiresType({UserTypes.COACH, UserTypes.VIP})
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
    @Log(title = "用户个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateInfo")
    public R<?> updateInfo(@RequestBody UserInfoUpdateDTO dto) {
        infoService.updateInfo(dto);
        return R.ok("个人信息修改成功");
    }

    /**
     * 修改密码
     */
    @Log(title = "用户密码", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePassword")
    public R<?> updatePassword(@RequestBody UserPasswordUpdateDTO dto) {
        infoService.updatePassword(dto);
        return R.ok("密码修改成功");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PutMapping("/updateAvatar")
    public R<?> updateAvatar(@RequestParam("avatarFile") MultipartFile mf) {
        infoService.updateAvatar(mf);
        return R.ok("头像更新成功");
    }
}
