package com.sql.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import com.sql.api.RemoteLoginLogService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.bo.CoachInviteBody;
import com.sql.common.entity.po.LoginLog;
import com.sql.common.entity.po.User;
import com.sql.common.enums.AccountStatus;
import com.sql.common.exception.ServiceException;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.UserTokenService;
import com.sql.common.entity.po.ClassHour;
import com.sql.user.dto.UserLoginDTO;
import com.sql.user.dto.UserRegisterDTO;
import com.sql.user.mapper.ClassHourMapper;
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
    private ClassHourMapper classHourMapper;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RemoteLoginLogService remoteLoginLogService;

    @Autowired
    private RedisService redisService;

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
     * 若用户不存在，返回null，由控制层引导前端进入注册流程
     */
    @Override
    public String login(UserLoginDTO dto) {
        User user;
        String openId = "unknown";
        String session_key;
        try {
            // TODO:先取消微信登录，进行用户测试
            // 请求微信接口获取 openId 和 session_key
            // Map<String, String> wxResult = getWxSession(dto.getCode());
            // openId = wxResult.get("openid");
            // session_key = wxResult.get("session_key");

            // String errcode = wxResult.get("errcode");

            // if (StringUtils.isNotEmpty(errcode) && !"0".equals(errcode)) {
            // String errmsg = wxResult.getOrDefault("errmsg", "微信登录失败");
            // throw new ServiceException("微信登录失败：" + errmsg);
            // }

            // if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(session_key)) {
            // throw new ServiceException("获取微信校验返回字段失败");
            // }

            // FIXME:临时测试
            openId = dto.getCode();
            session_key = UUID.randomUUID().toString().substring(0, 8);

            // 根据 openId 查询用户
            user = userMapper.selectByOpenId(openId);
            if (user == null) {
                // 用户不存在，返回null，引导前端进入注册流程
                return null;
            }

            if (AccountStatus.DISABLE.getCode().equals(user.getStatus())) {
                throw new ServiceException("账号已停用，请联系管理员");
            }
        } catch (Exception e) {
            log.error("登录失败", e);
            recordLoginLog(openId, AuthConstants.LOGIN_FAIL, "登录失败: " + e.getMessage());
            throw new ServiceException("登录失败: " + e.getMessage());
        }

        String accessToken = userTokenService.createToken(user, session_key);
        recordLoginLog(openId, AuthConstants.LOGIN_SUCCESS, "登录成功");

        return accessToken;
    }

    /**
     * 用户注册
     * 选择成为普通会员（userType=0）或教练（userType=1）
     * 教练注册需提供店铺管理员生成的邀请码，注册后直接绑定对应店铺
     */
    @Override
    public String register(UserRegisterDTO dto) {
        User user;
        String openId = "unknown";
        String session_key;
        try {
            // FIXME:临时测试
            // 请求微信接口获取 openId 和 session_key
            // Map<String, String> wxResult = getWxSession(dto.getCode());
            // openId = wxResult.get("openid");
            // session_key = wxResult.get("session_key");

            // String errcode = wxResult.get("errcode");
            // if (StringUtils.isNotEmpty(errcode) && !"0".equals(errcode)) {
            // String errmsg = wxResult.getOrDefault("errmsg", "微信登录失败");
            // throw new ServiceException("微信认证失败：" + errmsg);
            // }
            // if (StringUtils.isEmpty(openId) || StringUtils.isEmpty(session_key)) {
            // throw new ServiceException("获取微信校验返回字段失败");
            // }

            // FIXME:测试结束后删除
            openId = dto.getCode();
            session_key = UUID.randomUUID().toString().substring(0, 8);


            // 校验用户是否已存在
            User existUser = userMapper.selectByOpenId(openId);
            if (existUser != null) {
                throw new ServiceException("该用户已注册，请直接登录");
            }

            // 校验用户类型
            String userType = dto.getUserType();
            if (!"0".equals(userType) && !"1".equals(userType)) {
                throw new ServiceException("用户类型不合法");
            }

            user = new User();
            user.setOpenId(openId);
            user.setUserType(userType);
            user.setNickName("用户" + openId.substring(0, 6));

            // 教练注册：校验邀请码并绑定店铺
            if ("1".equals(userType)) {
                // 默认照片
                user.setPhoto("/default_coach_photo.jpg");

                String inviteCode = dto.getInviteCode();
                if (StringUtils.isEmpty(inviteCode)) {
                    throw new ServiceException("成为教练需要提供邀请码");
                }

                String inviteKey = AuthConstants.INVITE_COACH_CODE + inviteCode;
                CoachInviteBody inviteBody = redisService.getCacheObject(inviteKey);
                if (inviteBody == null) {
                    throw new ServiceException("邀请码无效或已过期");
                }

                // 绑定店铺
                user.setStoreId(inviteBody.getStoreId());

                // 注册成功后删除邀请码（一次性使用）
                String coachInviteKey = AuthConstants.INVITE_COACH + inviteBody.getReferrerId() + ":"
                        + inviteBody.getStoreId();
                redisService.deleteObject(coachInviteKey);
                redisService.deleteObject(inviteKey);
            }

            int rows = userMapper.insert(user);
            if (rows <= 0) {
                throw new ServiceException("注册失败，请联系管理员");
            }

            // 会员注册时初始化课时记录（remaining_hours=0，后续购买后增加）
            if ("0".equals(userType)) {
                ClassHour classHour = new ClassHour();
                classHour.setUserId(user.getUserId());
                classHourMapper.insert(classHour);
            }
        } catch (Exception e) {
            log.error("注册失败", e);
            recordLoginLog(openId, AuthConstants.LOGIN_FAIL, "注册失败: " + e.getMessage());
            throw new ServiceException("注册失败: " + e.getMessage());
        }

        // 创建token，直接完成登录
        String accessToken = userTokenService.createToken(user, session_key);
        recordLoginLog(openId, AuthConstants.REGISTER, "注册成功");

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
    public void recordLoginLog(String openId, String status, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setAccessTime(LocalDateTime.now());
        loginLog.setUsername(openId);
        loginLog.setIpAddr(IpUtils.getIpAddr());
        loginLog.setMsg(message);

        if (StringUtils.equalsAny(status, AuthConstants.LOGIN_SUCCESS, AuthConstants.LOGOUT, AuthConstants.REGISTER)) {
            loginLog.setStatus(AuthConstants.LOGIN_SUCCESS_STATUS);
        } else if (AuthConstants.LOGIN_FAIL.equals(status)) {
            loginLog.setStatus(AuthConstants.LOGIN_FAIL_STATUS);
        }
        remoteLoginLogService.saveLoginLog(loginLog, AuthConstants.INNER);
    }
}
