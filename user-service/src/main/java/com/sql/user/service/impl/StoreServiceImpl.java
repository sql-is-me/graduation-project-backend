package com.sql.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sql.common.entity.bo.CoachBriefInfo;
import com.sql.common.entity.bo.ManagerBriefInfo;
import com.sql.common.entity.bo.StoreBriefInfo;
import com.sql.common.entity.vo.StoreAndCoachInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.user.mapper.StoreMapper;
import com.sql.user.service.StoreService;
import com.sql.utils.StringUtils;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Value("${file.avatar-path}")
    private String avatarUrl;

    @Override
    public StoreAndCoachInfo getStoreInfo(Long storeId) {
        if (storeId == null) {
            storeId = ContextHolder.getUO().getUserInfo().getStoreId();
            if (storeId == null) {
                throw new ServiceException("当前用户未绑定店铺");
            }
        }

        StoreBriefInfo brief = storeMapper.selectStoreBriefById(storeId);
        if (brief == null) {
            throw new ServiceException("店铺信息不存在");
        }

        // 查询管理员列表并拼接头像 URL
        List<ManagerBriefInfo> managers = storeMapper.selectManagersByStoreId(storeId);
        managers.forEach(m -> {
            if (StringUtils.isNotEmpty(m.getAvatar())) {
                m.setAvatar(avatarUrl + m.getAvatar());
            }
        });

        // 查询教练列表并拼接头像 URL
        List<CoachBriefInfo> coaches = storeMapper.selectCoachesByStoreId(storeId);
        coaches.forEach(c -> {
            if (StringUtils.isNotEmpty(c.getAvatar())) {
                c.setAvatar(avatarUrl + c.getAvatar());
            }
        });

        return new StoreAndCoachInfo(brief, managers, coaches);
    }
}
