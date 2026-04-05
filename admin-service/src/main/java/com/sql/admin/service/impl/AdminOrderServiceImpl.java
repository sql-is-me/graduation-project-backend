package com.sql.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.OrderMapper;
import com.sql.admin.service.AdminOrderService;
import com.sql.common.entity.po.Order;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.utils.StringUtils;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> listAllOrders(Long storeId, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (storeId != null) {
            wrapper.eq(Order::getStoreId, storeId);
        }
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public List<Order> listStoreOrders(String status) {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStoreId, storeId);
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }
}
