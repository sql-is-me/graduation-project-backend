package com.sql.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.common.entity.bo.UserOnline;
import com.sql.common.entity.po.Order;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.user.mapper.OrderMapper;
import com.sql.user.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> listMyOrders(String status) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        UserOnline uo = ContextHolder.getUO();
        Long userId = uo.getUserInfo().getUserId();

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new ServiceException("无权查看此订单");
        }
        return order;
    }
}