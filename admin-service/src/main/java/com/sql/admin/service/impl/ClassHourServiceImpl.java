package com.sql.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.ClassHourMapper;
import com.sql.common.entity.db.ClassHour;
import com.sql.admin.service.ClassHourService;

@Service
public class ClassHourServiceImpl implements ClassHourService {

    @Autowired
    private ClassHourMapper classHourMapper;

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
}