package com.sql.admin.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.mapper.LoginInfoMapper;
import com.sql.admin.mapper.OperLogMapper;
import com.sql.admin.service.LogService;
import com.sql.common.entity.LoginInfo;
import com.sql.common.entity.OperLog;
import com.sql.utils.StringUtils;

public class LogServiceImpl implements LogService {

    @Autowired
    private OperLogMapper operLogMapper;

    @Autowired
    private LoginInfoMapper loginInfoMapper;

    // ==================== 操作日志 ====================

    /**
     * 查询操作日志列表（支持按操作人、标题、业务类型、操作状态筛选）
     */
    @Override
    public List<OperLog> listOperLog(OperLog query) {// TODO:待审查
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(query.getOperName()), OperLog::getOperName, query.getOperName())
                .like(StringUtils.isNotEmpty(query.getTitle()), OperLog::getTitle, query.getTitle())
                .eq(query.getBusinessType() != null, OperLog::getBusinessType, query.getBusinessType())
                .eq(query.getStatus() != null, OperLog::getStatus, query.getStatus())
                .eq(query.getOperatorType() != null, OperLog::getOperatorType, query.getOperatorType())
                .like(StringUtils.isNotEmpty(query.getOperIp()), OperLog::getOperIp, query.getOperIp())
                .orderByDesc(OperLog::getOperId);
        return operLogMapper.selectList(wrapper);
    }

    @Override
    public OperLog getOperLog(Long operId) {
        return operLogMapper.selectById(operId);
    }

    @Override
    public int deleteOperLog(Long operId) {
        return operLogMapper.deleteById(operId);
    }

    @Override
    public int insertOperLog(OperLog operLog) {
        return operLogMapper.insert(operLog);
    }

    /**
     * 清空操作日志
     */
    public void cleanOperLog() { // TODO: 待添加相关接口
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(OperLog::getOperId, 0);
        operLogMapper.delete(wrapper);
    }

    /**
     * 查询登录日志列表（支持按用户名、IP、状态筛选）
     */
    @Override
    public List<LoginInfo> listLoginInfo(LoginInfo query) {// TODO:待审查
        LambdaQueryWrapper<LoginInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(query.getUsername()), LoginInfo::getUsername, query.getUsername())
                .like(StringUtils.isNotEmpty(query.getIpaddr()), LoginInfo::getIpaddr, query.getIpaddr())
                .eq(StringUtils.isNotEmpty(query.getStatus()), LoginInfo::getStatus, query.getStatus())
                .orderByDesc(LoginInfo::getInfoId);
        return loginInfoMapper.selectList(wrapper);
    }

    @Override
    public LoginInfo getLoginInfo(Long infoId) {
        return loginInfoMapper.selectById(infoId);
    }

    @Override
    public int deleteLoginInfo(Long infoId) {
        return loginInfoMapper.deleteById(infoId);
    }

    public int insertLoginInfo(LoginInfo loginInfo) {
        return loginInfoMapper.insert(loginInfo);
    }

    /**
     * 批量删除登录日志
     */
    public int deleteLoginInfoByIds(Long[] infoIds) {// TODO: 待添加相关接口
        return loginInfoMapper.deleteByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空登录日志
     */
    public void cleanLoginInfo() {// TODO: 待添加相关接口
        LambdaQueryWrapper<LoginInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(LoginInfo::getInfoId, 0);
        loginInfoMapper.delete(wrapper);
    }
}
