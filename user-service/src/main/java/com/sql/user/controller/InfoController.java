package com.sql.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sql.common.auth.annotation.LoginRequired;
import com.sql.common.auth.annotation.RequiresType;
import com.sql.common.entity.result.R;
import com.sql.common.entity.vo.UserInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.log.annotation.Log;
import com.sql.common.log.enums.BusinessType;
import com.sql.user.dto.UserInfoUpdateDTO;
import com.sql.user.dto.UserUpdateEmailDTO;
import com.sql.user.service.InfoService;

/**
 * 用户/教练个人信息控制器
 * 教练和会员均可访问
 */
@RestController
@RequestMapping("/user/info")
@LoginRequired
@RequiresType({UserTypes.COACH, UserTypes.VIP})
public class InfoController {

    @Autowired
    private InfoService infoService;

    /**
     * 获取当前登录用户个人信息
     */
    @GetMapping
    public R<?> getInfo() {
        UserInfo userInfo = infoService.getInfo();
        return R.ok(userInfo);
    }

    /**
     * 修改个人信息
     *
     * nickName、phone、sex
     */
    @Log(title = "用户个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateInfo")
    public R<?> updateInfo(@Validated @RequestBody UserInfoUpdateDTO dto) {
        infoService.updateInfo(dto);
        return R.ok("个人信息修改成功");
    }

    /**
     * 修改邮箱
     */
    @Log(title = "用户邮箱", businessType = BusinessType.UPDATE)
    @PutMapping("/updateEmail")
    public R<?> updateEmail(@Validated @RequestBody UserUpdateEmailDTO dto) {
        infoService.updateEmail(dto);
        return R.ok("邮箱更新成功");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/updateAvatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<?> updateAvatar(@RequestPart("avatarFile") MultipartFile mf) {
        infoService.updateAvatar(mf);
        return R.ok("头像更新成功");
    }
}
