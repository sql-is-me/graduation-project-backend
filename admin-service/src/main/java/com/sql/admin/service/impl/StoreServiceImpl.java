package com.sql.admin.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.AdminMapper;
import com.sql.admin.mapper.StoreMapper;
import com.sql.admin.mapper.UserMapper;
import com.sql.admin.service.StoreService;
import com.sql.common.entity.dto.StoreCreateDTO;
import com.sql.common.entity.dto.StoreUpdateDTO;
import com.sql.common.entity.po.Admin;
import com.sql.common.entity.po.Store;
import com.sql.common.entity.po.User;
import com.sql.common.entity.vo.CoachesInfo;
import com.sql.common.entity.vo.StoreInfo;
import com.sql.common.entity.vo.VIPsInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.utils.StringUtils;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long createStore(StoreCreateDTO dto) {
        Long adminId = ContextHolder.getAO().getAdminInfo().getAdminId();

        if (storeMapper.selectByStoreName(dto.getStoreName()) != null) {
            throw new ServiceException("店铺名称已存在");
        }

        Store store = new Store();
        store.setCreatorId(adminId);
        store.setStoreName(dto.getStoreName());
        store.setAddress(dto.getAddress());

        int rows = storeMapper.insert(store);

        if (rows <= 0) {
            throw new ServiceException("创建店铺失败");
        }
        return store.getStoreId();
    }

    @Override
    public void updateStore(Long storeId, StoreUpdateDTO dto) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new ServiceException("店铺不存在");
        }

        if (StringUtils.isNotEmpty(dto.getStoreName()) && !dto.getStoreName().equals(store.getStoreName())) {
            if (storeMapper.selectByStoreName(dto.getStoreName()) != null) {
                throw new ServiceException("店铺名称已存在");
            }
            store.setStoreName(dto.getStoreName());
        }

        if (StringUtils.isNotEmpty(dto.getAddress())) {
            store.setAddress(dto.getAddress());
        }

        int rows = storeMapper.updateById(store);
        if (rows <= 0) {
            throw new ServiceException("店铺信息更新失败");
        }
    }

    @Override
    public void deleteStore(Long storeId) {
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new ServiceException("店铺不存在");
        }
        if ("1".equals(store.getStatus())) {
            throw new ServiceException("店铺已处于停业状态");
        }

        int rows = storeMapper.updateStatus(storeId, "1");
        if (rows <= 0) {
            throw new ServiceException("店铺状态信息更新失败");
        }
    }

    @Override
    public void setOwner(Long storeId, Long ownerId) {
        Long adminId = ContextHolder.getAO().getAdminInfo().getAdminId();

        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new ServiceException("店铺不存在");
        }
        // 仅创建人可设置所有人
        if (!adminId.equals(store.getCreatorId())) {
            throw new ServiceException("仅店铺创建人可设置所有人");
        }

        // 校验目标用户是否为MANAGER
        Admin target = adminMapper.selectById(ownerId);
        if (target == null) {
            throw new ServiceException("目标管理员不存在");
        }
        if (!"1".equals(target.getAdminType())) {
            throw new ServiceException("仅可将MANAGER类型的管理员设置为店铺所有人");
        }

        int rows = storeMapper.updateOwnerId(storeId, ownerId);
        if (rows <= 0) {
            throw new ServiceException("店铺所有人更新失败");
        }
    }

    @Override
    public List<Store> listStores(String status) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq(Store::getStatus, status);
        }

        return storeMapper.selectList(wrapper);
    }

    @Override
    public StoreInfo getStoreById(Long storeId) {
        StoreInfo storeInfo = storeMapper.selectStoreInfoById(storeId);
        if (storeInfo == null) {
            throw new ServiceException("店铺不存在");
        }
        return storeInfo;
    }

    @Override
    public List<VIPsInfo> listStoreVIPs() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }

        // 查询本店所有会员（userType=0）
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getStoreId, storeId)
                .eq(User::getUserType, "0")
                .orderByDesc(User::getCreateTime);
        List<User> vips = userMapper.selectList(userWrapper);

        if (vips.isEmpty()) {
            return new ArrayList<>();
        }

        List<VIPsInfo> vipsInfo = vips
                .stream()
                .map(VIPsInfo::new)
                .collect(Collectors.toList());

        return vipsInfo;
    }

    @Override
    public List<CoachesInfo> listStoreCoachs() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }

        // 查询本店所有教练
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getStoreId, storeId)
                .eq(User::getUserType, "1")
                .orderByDesc(User::getCreateTime);
        List<User> coaches = userMapper.selectList(userWrapper);

        if (coaches.isEmpty()) {
            return new ArrayList<>();
        }

        List<CoachesInfo> coachesInfo = coaches
                .stream()
                .map(CoachesInfo::new)
                .collect(Collectors.toList());

        return coachesInfo;
    }
}
