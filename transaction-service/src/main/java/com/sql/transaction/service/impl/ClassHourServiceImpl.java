package com.sql.transaction.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.common.entity.db.ClassHour;
import com.sql.common.entity.db.User;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.transaction.mapper.ClassHourMapper;
import com.sql.transaction.mapper.UserMapper;
import com.sql.transaction.service.ClassHourService;

@Service
public class ClassHourServiceImpl implements ClassHourService {

    @Autowired
    private ClassHourMapper classHourMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public int addClassHours(Long userId, int hours) {
        LambdaQueryWrapper<ClassHour> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClassHour::getUserId, userId);
        ClassHour classHour = classHourMapper.selectOne(wrapper);

        if (classHour == null) {
            classHour = new ClassHour();
            classHour.setUserId(userId);
            classHour.setHours(hours);
            classHour.setUsedHours(0);
            classHour.setRemainingHours(hours);
            return classHourMapper.insert(classHour);
        } else {
            classHour.setHours(classHour.getHours() + hours);
            classHour.setRemainingHours(classHour.getRemainingHours() + hours);
            return classHourMapper.updateById(classHour);
        }
    }

    @Override
    public List<ClassHour> listClassHours() {
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

        List<Long> memberIds = members.stream().map(User::getUserId).toList();
        LambdaQueryWrapper<ClassHour> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ClassHour::getUserId, memberIds);
        return classHourMapper.selectList(wrapper);
    }
}
