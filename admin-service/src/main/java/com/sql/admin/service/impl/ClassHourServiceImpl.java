package com.sql.admin.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.service.ClassHourService;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.po.User;
import com.sql.common.entity.vo.ClassHoursInfo;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.admin.mapper.ClassHourMapper;
import com.sql.admin.mapper.UserMapper;

@Service
public class ClassHourServiceImpl implements ClassHourService {

    @Autowired
    private ClassHourMapper classHourMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void addClassHours(Long userId, int hours) {
        LambdaQueryWrapper<ClassHour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassHour::getUserId, userId);
        ClassHour classHour = classHourMapper.selectOne(wrapper);

        classHour.setHours(classHour.getHours() + hours);
        classHour.setRemainingHours(classHour.getRemainingHours() + hours);

        int rows = classHourMapper.updateById(classHour);
        if (rows <= 0) {
            throw new ServiceException("课时到账失败，请联系管理员");
        }

    }

    @Override
    public List<ClassHoursInfo> listClassHours() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }

        // 查询当前店铺下的所有会员
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getStoreId, storeId)
                .eq(User::getUserType, "0"); // 会员
        List<User> members = userMapper.selectList(userWrapper);
        if (members.isEmpty()) {
            return new ArrayList<>();
        }

        // 构建 userId -> nickName 映射
        Map<Long, String> userNickMap = members.stream()
                .collect(Collectors.toMap(User::getUserId, User::getNickName));

        List<Long> memberIds = new ArrayList<>(userNickMap.keySet());
        LambdaQueryWrapper<ClassHour> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ClassHour::getUserId, memberIds);

        List<ClassHour> classHours = classHourMapper.selectList(wrapper);
        return classHours.stream()
                .map(ch -> new ClassHoursInfo(ch, userNickMap.get(ch.getUserId())))
                .collect(Collectors.toList());
    }
}
