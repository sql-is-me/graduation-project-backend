package com.ruoyi.admin.service.Impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.admin.service.MonitorService;
import com.ruoyi.common.Constants.TokenConstants;
import com.ruoyi.common.VO.OnlineUserInfo;
import com.ruoyi.common.entity.AdminOnline;
import com.ruoyi.common.enums.UserTypes;
import com.ruoyi.common.redis.service.RedisService;

/**
 * 管理员监控服务
 * 供顶级管理员使用，可查看/删除操作日志与登录日志并强制用户下线
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private RedisService redisService;

    public List<OnlineUserInfo> getOnlineAdmins() {
        List<OnlineUserInfo> onlineList = new ArrayList<>();

        Collection<String> keys = redisService.keys(TokenConstants.TOKENS + "*");
        for (String key : keys) {
            AdminOnline ao = redisService.getCacheObject(key);
            if (ao == null || ao.getAdminInfo() == null) {
                continue;
            }

            OnlineUserInfo online = new OnlineUserInfo();
            online.setToken(ao.getToken());
            online.setUserId(ao.getAdminInfo().getAdminId());
            online.setUserName(ao.getAdminInfo().getUsername());
            online.setNickName(ao.getAdminInfo().getNickName());
            online.setIpaddr(ao.getIpaddr());
            online.setLoginTime(ao.getLoginTime());

            UserTypes userType = UserTypes.fromCode(ao.getAdminInfo().getAdminType());
            online.setUserType(userType != null ? userType.name() : null);

            onlineList.add(online);
        }
        onlineList.sort(Comparator.comparing(OnlineUserInfo::getLoginTime).reversed());
        return onlineList;
    }

    public List<OnlineUserInfo> getOnlineUsers() {
        List<OnlineUserInfo> onlineList = new ArrayList<>();

        // Collection<String> keys = redisService.keys(CacheConstants.ADMIN_TOKEN_KEY +
        // "*");
        // for (String key : keys) {
        // AdminOnline ao = redisService.getCacheObject(key);
        // if (ao == null || ao.getAdminInfo() == null) {
        // continue;
        // }

        // OnlineUserInfo online = new OnlineUserInfo();
        // online.setTokenId(ao.getToken());
        // online.setUserId(ao.getAdminInfo().getAdminId());
        // online.setUserName(ao.getAdminInfo().getUsername());
        // online.setNickName(ao.getAdminInfo().getNickName());
        // online.setIpaddr(ao.getIpaddr());
        // online.setLoginTime(ao.getLoginTime());

        // UserTypes userType = UserTypes.fromCode(ao.getAdminInfo().getAdminType());
        // online.setUserType(userType != null ? userType.name() : null);

        // onlineList.add(online);
        // }
        // onlineList.sort(Comparator.comparing(OnlineUserInfo::getLoginTime).reversed());
        return onlineList;
    }

    public void forceAdminLogout(String token) {
        redisService.deleteObject(TokenConstants.TOKENS + token);
    }

    public void forceUserLogout(String token) {
        // redisService.deleteObject(TokenConstants.USER_TOKEN_KEY + token);
        // TODO: 用户强退功能后续完善
    }
}
