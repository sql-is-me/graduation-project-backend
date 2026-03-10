package com.ruoyi.admin.controller;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.admin.dto.AdminPasswordUpdateDTO;
import com.ruoyi.admin.dto.AdminInfoUpdateDTO;
import com.ruoyi.admin.service.Impl.InfoServiceImpl;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.file.FileTypeUtils;
import com.ruoyi.common.core.utils.file.MimeTypeUtils;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresLogin;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.common.tokens.TokenService;
import com.ruoyi.system.api.RemoteFileService;
import com.ruoyi.system.api.domain.SysFile;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 管理员个人信息控制器
 */
@RestController
@RequestMapping("/admin/info")
public class InfoController {

    @Autowired
    private InfoServiceImpl infoService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 获取管理员个人信息
     */
    @GetMapping
    public R<?> getInfo(HttpServletRequest request) {
        SysUser user = infoService.getInfo(request);
        return R.ok(user);
    }

    /**
     * 修改管理员个人信息
     */
    @RequiresLogin
    @Log(title = "管理员个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<?> updateProfile(@RequestBody AdminInfoUpdateDTO dto) {
        profileService.updateProfile(dto);
        return R.ok("修改成功");
    }

    /**
     * 修改管理员密码
     */
    @RequiresLogin
    @Log(title = "管理员修改密码", businessType = BusinessType.UPDATE)
    @PutMapping("/password")
    public R<?> updatePassword(@RequestBody AdminPasswordUpdateDTO dto) {
        profileService.updatePassword(dto);
        return R.ok("密码修改成功");
    }

    /**
     * 管理员头像上传
     */
    @RequiresLogin
    @Log(title = "管理员头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public R<?> avatar(@RequestParam("avatarfile") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String extension = FileTypeUtils.getExtension(file);
        if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
            return R.fail("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
        }
        R<SysFile> fileResult = remoteFileService.upload(file);
        if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData())) {
            return R.fail("文件服务异常，请联系管理员");
        }
        String url = fileResult.getData().getUrl();
        profileService.updateAvatar(loginUser.getUserid(), url);

        // 删除旧头像
        String oldAvatarUrl = loginUser.getSysUser().getAvatar();
        if (StringUtils.isNotEmpty(oldAvatarUrl)) {
            remoteFileService.delete(oldAvatarUrl);
        }
        // 更新缓存
        loginUser.getSysUser().setAvatar(url);
        tokenService.setLoginUser(loginUser);

        return R.ok(url, "上传成功");
    }
}
