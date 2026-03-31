package com.sql.admin.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sql.admin.service.MonitorService;
import com.sql.common.constants.HttpStatusConstants;
import com.sql.common.entity.bo.AdminOnline;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.constants.TokenConstants;
import com.sql.common.entity.vo.OnlineUserInfo;
import com.sql.common.entity.vo.TableDataInfo;
import com.sql.common.enums.UserTypes;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.AdminTokenService;

/**
 * 管理员监控服务
 * 供系统管理员使用，可查看/删除操作日志与登录日志并强制用户下线
 */
@Service
public class MonitorServiceImpl implements MonitorService {
    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private RedisService redisService;

    @Override
    public TableDataInfo getOnlineAdmins(int pageNum, int pageSize, String orderByColumn, boolean asc) {
        List<OnlineUserInfo> onlineList = new ArrayList<>();

        Collection<String> keys = redisService.keys(TokenConstants.ADMIN_TOKENS + "*");
        for (String key : keys) {
            AdminOnline ao = redisService.getCacheObject(key);
            if (ao == null || ao.getAdminInfo() == null) {
                continue;
            }

            OnlineUserInfo online = new OnlineUserInfo();
            online.setUserId(ao.getAdminInfo().getAdminId());
            online.setUserName(ao.getAdminInfo().getUsername());
            online.setNickName(ao.getAdminInfo().getNickName());
            online.setIpaddr(ao.getIpaddr());
            online.setLoginTime(ao.getLoginTime());

            UserTypes userType = UserTypes.fromCode(ao.getAdminInfo().getAdminType());
            online.setUserType(userType != null ? userType.name() : null);

            onlineList.add(online);
        }
        onlineList.sort(buildComparator(orderByColumn, asc));
        return buildPage(onlineList, pageNum, pageSize);
    }

    @Override
    public TableDataInfo getOnlineUsers(int pageNum, int pageSize, String orderByColumn, boolean asc) {
        List<OnlineUserInfo> onlineList = new ArrayList<>();

        Collection<String> keys = redisService.keys(TokenConstants.USER_TOKENS + "*");
        for (String key : keys) {
            UserOnline uo = redisService.getCacheObject(key);
            if (uo == null || uo.getUserInfo() == null) {
                continue;
            }

            OnlineUserInfo online = new OnlineUserInfo();
            online.setUserId(uo.getUserInfo().getUserId());
            online.setUserName(uo.getUserInfo().getUsername());
            online.setNickName(uo.getUserInfo().getNickName());
            online.setIpaddr(uo.getIpaddr());
            online.setLoginTime(uo.getLoginTime());

            UserTypes userType = UserTypes.fromCode(uo.getUserInfo().getUserType());
            online.setUserType(userType != null ? userType.name() : null);

            onlineList.add(online);
        }
        onlineList.sort(buildComparator(orderByColumn, asc));
        return buildPage(onlineList, pageNum, pageSize);
    }

    private Comparator<OnlineUserInfo> buildComparator(String orderByColumn, boolean asc) {
        Comparator<OnlineUserInfo> comparator = "loginTime".equalsIgnoreCase(orderByColumn)
                ? Comparator.comparing(OnlineUserInfo::getLoginTime)
                : Comparator.comparing(OnlineUserInfo::getUserId);
        return asc ? comparator : comparator.reversed();
    }

    private TableDataInfo buildPage(List<?> all, int pageNum, int pageSize) {
        int total = all.size();
        int fromIndex = (pageNum - 1) * pageSize;
        List<?> page = fromIndex >= total
                ? Collections.emptyList()
                : all.subList(fromIndex, Math.min(fromIndex + pageSize, total));
        TableDataInfo result = new TableDataInfo();
        result.setCode(HttpStatusConstants.SUCCESS);
        result.setMsg("查询成功");
        result.setRows(page);
        result.setTotal(total);
        return result;
    }

    @Override
    public void forceAdminLogout(String adminId) {
        adminTokenService.checkAndDeleteCacheObject(Long.parseLong(adminId));
    }

    @Override
    public void forceUserLogout(String token) {// FIXME:需要添加对应token-id映射
        redisService.deleteObject(TokenConstants.USER_TOKENS + token);
    }
}
