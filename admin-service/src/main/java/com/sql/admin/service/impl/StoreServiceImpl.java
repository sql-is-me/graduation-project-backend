package com.sql.admin.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.AdminMapper;
import com.sql.admin.mapper.ChildMapper;
import com.sql.admin.mapper.ClassHourMapper;
import com.sql.admin.mapper.StoreMapper;
import com.sql.admin.mapper.UserMapper;
import com.sql.admin.service.StoreService;
import com.sql.common.constants.AuthConstants;
import com.sql.common.entity.bo.AdminOnline;
import com.sql.common.entity.bo.BindStoreBody;
import com.sql.common.entity.dto.StoreCreateDTO;
import com.sql.common.entity.dto.StoreUpdateDTO;
import com.sql.common.entity.po.Admin;
import com.sql.common.entity.po.Child;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.po.Store;
import com.sql.common.entity.po.User;
import com.sql.common.entity.vo.ChildInfo;
import com.sql.common.entity.vo.CoachInfo;
import com.sql.common.entity.vo.CoachesInfo;
import com.sql.common.entity.vo.StoreInfo;
import com.sql.common.entity.vo.VIPInfo;
import com.sql.common.entity.vo.VIPsInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.common.redis.service.RedisService;
import com.sql.common.tokens.AdminTokenService;
import com.sql.utils.StringUtils;
import com.sql.utils.file.FileUtils;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreMapper storeMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChildMapper childMapper;

    @Autowired
    private ClassHourMapper classHourMapper;

    @Autowired
    private AdminTokenService adminTokenService;

    @Autowired
    private RedisService redisService;

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

        // 检验所指定的管理员是否已绑定店铺（一个管理员只能管理一个店铺），已经注销的店铺不算
        LambdaQueryWrapper<Store> ownerCheck = new LambdaQueryWrapper<>();
        ownerCheck.eq(Store::getOwnerId, ownerId).eq(Store::getStatus, "0");
        if (storeMapper.selectCount(ownerCheck) > 0) {
            throw new ServiceException("目标管理员已绑定其他店铺");
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
    public VIPInfo getVIPInfo(Long vipId) {
        User vip = userMapper.selectById(vipId);
        if (vip == null || !"0".equals(vip.getUserType())) {
            throw new ServiceException("该会员不存在");
        }

        // 查询孩子
        List<Child> children = childMapper.selectByParentId(vipId);

        // 查询课时
        ClassHour classHour = classHourMapper.selectByVIPId(vipId);

        VIPInfo vipInfo = new VIPInfo(vip, classHour, children);
        vipInfo.setAvatar(FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, vip.getAvatar()));
        return vipInfo;
    }

    @Override
    public List<CoachesInfo> listStoreCoaches() {
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

        List<CoachesInfo> coachesInfos = new ArrayList<>();
        for (User coach : coaches) {
            CoachesInfo coachesInfo = new CoachesInfo(coach);
            coachesInfo.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_COACH_PHOTO, coach.getPhoto()));

            coachesInfos.add(coachesInfo);
        }

        return coachesInfos;
    }

    @Override
    public CoachInfo getCoachInfo(Long coachId) {
        User coach = userMapper.selectById(coachId);
        if (coach == null || !"1".equals(coach.getUserType())) {
            throw new ServiceException("该教练不存在");
        }

        CoachInfo coachInfo = new CoachInfo(coach);
        coachInfo.setAvatar(FileUtils.toAbsoluteUrl(FileUtils.TYPE_AVATAR, coach.getAvatar()));
        coachInfo.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_COACH_PHOTO, coach.getPhoto()));
        return coachInfo;
    }

    @Override
    public List<ChildInfo> listStoreChildren() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }

        // 先查本店所有会员ID和姓名
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getStoreId, storeId).eq(User::getUserType, "0").select(User::getUserId, User::getNickName);
        List<User> parents = userMapper.selectList(userWrapper);

        if (parents.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, String> parentNameMap = parents.stream()
                .collect(Collectors.toMap(User::getUserId, User::getNickName));
        List<Long> parentIds = new ArrayList<>(parentNameMap.keySet());

        // 查这些会员旗下的所有孩子
        LambdaQueryWrapper<Child> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(Child::getParentId, parentIds).orderByAsc(Child::getParentId);
        List<Child> children = childMapper.selectList(childWrapper);

        List<ChildInfo> list = new ArrayList<>();
        for (Child child : children) {
            ChildInfo childInfo = new ChildInfo(child);
            childInfo.setPhoto(FileUtils.toAbsoluteUrl(FileUtils.TYPE_CHILD_PHOTO, child.getPhoto()));
            childInfo.setParentName(parentNameMap.get(child.getParentId()));

            list.add(childInfo);
        }

        return list;
    }

    /**
     * 生成绑定店铺邀请码
     * 用以提供给vip或coach通过邀请码申请绑定店铺
     */
    @Override
    public String generateBindStoreCode(HttpServletRequest request) {
        AdminOnline ao = adminTokenService.getAO(adminTokenService.getAOToken(request));
        Admin admin = ao.getAdminInfo();

        Long storeId = admin.getStoreId();
        if (storeId == null) {
            throw new ServiceException("未绑定门店，无法生成绑定店铺邀请码");
        }

        // 校验门店营业状态
        Store store = storeMapper.selectById(storeId);
        if (store == null) {
            throw new ServiceException("所属门店不存在");
        }
        if ("1".equals(store.getStatus())) {
            throw new ServiceException("所属门店已停业，无法生成绑定店铺邀请码");
        }

        // 防重复生成：同一管理员+门店若已有有效邀请码则直接复用
        String bindStoreInviteKey = AuthConstants.BIND_STORE_CODE + admin.getAdminId() + ":" + storeId;
        String existingCode = redisService.getCacheObject(bindStoreInviteKey);
        if (existingCode != null) {
            return existingCode;
        }

        // 生成8位大写邀请码
        String inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String inviteKey = AuthConstants.BIND_STORE_CODE + inviteCode;
        BindStoreBody inviteBody = new BindStoreBody(admin.getAdminId(), storeId);

        redisService.setCacheObject(inviteKey, inviteBody, AuthConstants.BIND_STORE_EXPIRE, TimeUnit.MINUTES);
        redisService.setCacheObject(bindStoreInviteKey, inviteCode, AuthConstants.BIND_STORE_EXPIRE, TimeUnit.MINUTES);

        return inviteCode;
    }

}
