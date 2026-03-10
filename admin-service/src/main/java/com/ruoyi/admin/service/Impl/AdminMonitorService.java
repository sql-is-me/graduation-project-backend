package com.ruoyi.admin.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.admin.mapper.LoginInfoMapper;
import com.ruoyi.admin.mapper.OperLogMapper;
import com.ruoyi.admin.mapper.AdminMapper;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 管理员监控服务（在线用户、日志查询、日志管理）
 * 供顶级管理员使用，可查看/删除/清空操作日志与登录日志
 */
@Service
public class AdminMonitorService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AdminMapper adminUserMapper;

    // ==================== 在线用户 ====================

    /**
     * 查询在线用户列表
     */
    public List<OnlineUserInfo> getOnlineUsers(String ipaddr, String userName) {
        Collection<String> keys = redisService.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<OnlineUserInfo> onlineList = new ArrayList<>();
        for (String key : keys) {
            LoginUser user = redisService.getCacheObject(key);
            if (user == null || user.getSysUser() == null) {
                continue;
            }
            boolean matchIp = StringUtils.isEmpty(ipaddr) || StringUtils.contains(user.getIpaddr(), ipaddr);
            boolean matchName = StringUtils.isEmpty(userName) || StringUtils.contains(user.getUsername(), userName);
            if (matchIp && matchName) {
                OnlineUserInfo info = new OnlineUserInfo();
                info.setTokenId(user.getToken());
                info.setUserName(user.getUsername());
                info.setIpaddr(user.getIpaddr());
                info.setLoginTime(user.getLoginTime());
                if (user.getSysUser() != null) {
                    info.setUserId(user.getSysUser().getUserId());
                    info.setNickName(user.getSysUser().getNickName());
                    info.setAdminType(user.getSysUser().getAdminType());
                }
                onlineList.add(info);
            }
        }
        Collections.reverse(onlineList);
        return onlineList;
    }

    /**
     * 强退用户
     */
    public void forceLogout(String tokenId) {
        redisService.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
    }

    /**
     * 解锁用户登录（清除密码错误次数缓存）
     */
    public void unlockUser(String userName) {
        redisService.deleteObject(CacheConstants.PWD_ERR_CNT_KEY + userName);
    }

    // ==================== 管理员用户 ====================

    /**
     * 查询管理员用户列表
     */
    public List<SysUser> listAdminUsers(SysUser user) {
        return adminUserMapper.selectAdminUserList(user);
    }

    
}
