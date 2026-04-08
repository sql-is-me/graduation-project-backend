package com.sql.user.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.sql.utils.file.FileUtils;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

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
            m.setAvatar(FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, m.getAvatar()));
        });

        // 查询教练列表并拼接头像 URL 和展示照片 URL
        List<CoachBriefInfo> coaches = storeMapper.selectCoachesByStoreId(storeId);
        coaches.forEach(c -> {
            c.setAvatar(FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, c.getAvatar()));
            c.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_COACH_PHOTO, c.getPhoto()));
        });

        return new StoreAndCoachInfo(brief, managers, coaches);
    }

    @Override
    public List<StoreBriefInfo> searchStores(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            return Collections.emptyList();
        }
        return storeMapper.searchByName(keyword.trim());
    }
}
