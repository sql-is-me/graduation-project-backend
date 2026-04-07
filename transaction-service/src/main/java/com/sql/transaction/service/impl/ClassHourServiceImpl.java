package com.sql.transaction.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.common.entity.po.ClassHour;
import com.sql.common.entity.po.User;
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
