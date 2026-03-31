package com.sql.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

import com.sql.api.RemoteLoginLogService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.po.LoginInfo;
import com.sql.common.entity.po.User;
import com.sql.common.enums.AccountStatus;
import com.sql.common.exception.ServiceException;
import com.sql.common.tokens.UserTokenService;
import com.sql.user.dto.UserLoginDTO;
import com.sql.user.mapper.UserMapper;
import com.sql.user.service.AuthService;
import com.sql.utils.IpUtils;
import com.sql.utils.StringUtils;

/**
 * 用户/教练登录与注册服务
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RemoteLoginLogService remoteLoginLogService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 小程序id
     */
    @Value("${wechat.app-id}")
    private String appId;

    /**
     * 小程序密钥
     */
    @Value("${wechat.app-secret}")
    private String appSecret;

    /**
     * 微信登录凭证校验接口地址
     */
    private static final String WECHAT_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 用户/教练登录（微信小程序）
     */
    @Override
    public String login(UserLoginDTO dto) {
        User user;
        String openId = "unknown";
        String session_key;
        try {
            // 请求微信接口获取 openId 和 session_key
            Map<String, String> wxResult = getWxSession(dto.getCode());
            openId = wxResult.get("openid");
            session_key = wxResult.get("session_key");

            String errcode = wxResult.get("errcode");

            if (StringUtils.isNotEmpty(errcode) && !"0".equals(errcode)) {
                String errmsg = wxResult.getOrDefault("errmsg", "微信登录失败");
                throw new ServiceException("微信登录失败：" + errmsg);
            }

            if (StringUtils.isEmpty(openId)) {
                throw new ServiceException("获取微信openId失败");
            }

            if (StringUtils.isEmpty(session_key)) {
                throw new ServiceException("获取微信session_key失败");
            }

            // 根据 openId 查询或创建用户
            user = userMapper.selectByOpenId(openId);
            if (user == null) {
                user = new User();
                user.setOpenId(openId);
                user.setUnionId(wxResult.get("unionid"));

                user.setUserType("0"); // 默认普通会员，后续申请成为教练

                int rows = userMapper.insert(user);
                if (rows <= 0) {
                    throw new ServiceException("创建用户失败，请联系管理员");
                }
            } else {
                if (AccountStatus.DISABLE.getCode().equals(user.getStatus())) {
                    throw new ServiceException("账号已停用，请联系管理员");
                }
            }
        } catch (Exception e) {
            log.error("登录失败", e);
            recordLoginInfo(openId, AuthConstants.LOGIN_FAIL, "登录失败: " + e.getMessage());
            throw new ServiceException("登录失败: " + e.getMessage());
        }

        String accessToken = userTokenService.createToken(user, session_key);
        recordLoginInfo(openId, AuthConstants.LOGIN_SUCCESS, "登录成功");

        return accessToken;
    }

    /**
     * 登录凭证校验。通过 wx.login 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程
     * 请求微信 jscode2session 接口，返回包含 openId、session_key、unionid 等字段的 Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> getWxSession(String code) {
        String url = UriComponentsBuilder.fromUri(java.net.URI.create(WECHAT_LOGIN_URL))
                .queryParam("appid", appId)
                .queryParam("secret", appSecret)
                .queryParam("js_code", code)
                .queryParam("grant_type", "authorization_code")
                .toUriString();
        Map<String, String> result = restTemplate.getForObject(url, Map.class);
        return result != null ? result : new HashMap<>();
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(String openId, String status, String message) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setAccessTime(LocalDateTime.now());
        loginInfo.setUsername(openId);
        loginInfo.setIpAddr(IpUtils.getIpAddr());
        loginInfo.setMsg(message);

        if (StringUtils.equalsAny(status, AuthConstants.LOGIN_SUCCESS, AuthConstants.LOGOUT, AuthConstants.REGISTER)) {
            loginInfo.setStatus(AuthConstants.LOGIN_SUCCESS_STATUS);
        } else if (AuthConstants.LOGIN_FAIL.equals(status)) {
            loginInfo.setStatus(AuthConstants.LOGIN_FAIL_STATUS);
        }
        remoteLoginLogService.saveLoginInfo(loginInfo, AuthConstants.INNER);
    }
}
