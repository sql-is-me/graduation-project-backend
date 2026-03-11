package com.ruoyi.admin.service.Impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.admin.mapper.LoginInfoMapper;
import com.ruoyi.admin.mapper.OperLogMapper;
import com.ruoyi.admin.service.LogService;
import com.ruoyi.common.entity.LoginInfo;
import com.ruoyi.common.entity.OperLog;
import com.ruoyi.common.core.utils.StringUtils;

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
    public List<OperLog> listOperLog(OperLog query) {// TODO:待审查与添加分页
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
    public List<LoginInfo> listLoginInfo(LoginInfo query) {// TODO:待审查与添加分页
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
