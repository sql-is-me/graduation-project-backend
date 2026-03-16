package com.sql.admin.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.admin.service.MonitorService;
import com.sql.common.constants.TokenConstants;
import com.sql.common.vo.OnlineUserInfo;
import com.sql.common.entity.AdminOnline;
import com.sql.common.entity.UserOnline;
import com.sql.common.enums.UserTypes;
import com.sql.common.redis.service.RedisService;

/**
 * 管理员监控服务
 * 供顶级管理员使用，可查看/删除操作日志与登录日志并强制用户下线
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private RedisService redisService;

    @Override
    public List<OnlineUserInfo> getOnlineAdmins() {
        List<OnlineUserInfo> onlineList = new ArrayList<>();

        Collection<String> keys = redisService.keys(TokenConstants.ADMIN_TOKENS + "*");
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

    @Override
    public List<OnlineUserInfo> getOnlineUsers() {
        List<OnlineUserInfo> onlineList = new ArrayList<>();

        Collection<String> keys = redisService.keys(TokenConstants.USER_TOKENS + "*");
        for (String key : keys) {
            UserOnline uo = redisService.getCacheObject(key);
            if (uo == null || uo.getUserInfo() == null) {
                continue;
            }

            OnlineUserInfo online = new OnlineUserInfo();
            online.setToken(uo.getToken());
            online.setUserId(uo.getUserInfo().getUserId());
            online.setUserName(uo.getUserInfo().getUsername());
            online.setNickName(uo.getUserInfo().getNickName());
            online.setIpaddr(uo.getIpaddr());
            online.setLoginTime(uo.getLoginTime());

            UserTypes userType = UserTypes.fromCode(uo.getUserInfo().getUserType());
            online.setUserType(userType != null ? userType.name() : null);

            onlineList.add(online);
        }
        onlineList.sort(Comparator.comparing(OnlineUserInfo::getLoginTime).reversed());
        return onlineList;
    }

    @Override
    public void forceAdminLogout(String token) {
        redisService.deleteObject(TokenConstants.ADMIN_TOKENS + token);
    }

    @Override
    public void forceUserLogout(String token) {
        redisService.deleteObject(TokenConstants.USER_TOKENS + token);
    }
}
