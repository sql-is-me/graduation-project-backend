package com.sql.gateway.service;

import java.io.IOException;

import com.sql.common.entity.result.AjaxResult;
import com.sql.common.exception.CaptchaException;

/**
 * 验证码处理
 */
public interface ValidateCodeService {
    /**
     * 生成验证码
     */
    public AjaxResult createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;
}
