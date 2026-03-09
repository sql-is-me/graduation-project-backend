package com.ruoyi.admin.service.Impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.admin.mapper.AdminLogininforMapper;
import com.ruoyi.admin.mapper.AdminOperLogMapper;
import com.ruoyi.admin.mapper.AdminMapper;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.system.api.domain.SysLogininfor;
import com.ruoyi.system.api.domain.SysOperLog;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 管理员监控服务（在线用户、日志查询）
 */
@Service
public class AdminMonitorService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AdminOperLogMapper operLogMapper;

    @Autowired
    private AdminLogininforMapper logininforMapper;

    @Autowired
    private AdminMapper adminUserMapper;

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
     * 查询操作日志列表
     */
    public List<SysOperLog> listOperLog(SysOperLog operLog) {
        return operLogMapper.selectOperLogList(operLog);
    }

    /**
     * 查询操作日志详情
     */
    public SysOperLog getOperLogById(Long operId) {
        return operLogMapper.selectOperLogById(operId);
    }

    /**
     * 查询登录日志列表
     */
    public List<SysLogininfor> listLogininfor(SysLogininfor logininfor) {
        return logininforMapper.selectLogininforList(logininfor);
    }

    /**
     * 解锁用户登录（清除密码错误次数缓存）
     */
    public void unlockUser(String userName) {
        redisService.deleteObject(CacheConstants.PWD_ERR_CNT_KEY + userName);
    }

    /**
     * 查询管理员用户列表
     */
    public List<SysUser> listAdminUsers(SysUser user) {
        return adminUserMapper.selectAdminUserList(user);
    }

    /**
     * 在线用户信息VO
     */
    @lombok.Data
    public static class OnlineUserInfo {
        private String tokenId;
        private Long userId;
        private String userName;
        private String nickName;
        private String ipaddr;
        private Long loginTime;
        private String adminType;
    }
}
