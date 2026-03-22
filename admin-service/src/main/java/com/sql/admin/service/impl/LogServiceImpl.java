package com.sql.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.dto.LoginInfoSelectDTO;
import com.sql.admin.dto.OperLogSelectDTO;
import com.sql.admin.mapper.LoginInfoMapper;
import com.sql.admin.mapper.OperLogMapper;
import com.sql.admin.service.LogService;
import com.sql.common.entity.db.LoginInfo;
import com.sql.common.entity.db.OperLog;
import com.sql.common.exception.ServiceException;
import com.sql.utils.StringUtils;
import org.springframework.stereotype.Service;

@Service
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
    public List<OperLog> listOperLog(OperLogSelectDTO dto) {
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dto.getOperatorType() != null, OperLog::getOperatorType, dto.getOperatorType())
                .like(StringUtils.isNotEmpty(dto.getOperName()), OperLog::getOperName, dto.getOperName())
                .like(StringUtils.isNotEmpty(dto.getOperIp()), OperLog::getOperIp, dto.getOperIp())
                .eq(StringUtils.isNotEmpty(dto.getStatus()), OperLog::getStatus, dto.getStatus())
                .orderByDesc(OperLog::getOperId);

        return operLogMapper.selectList(wrapper);
    }

    @Override
    public OperLog getOperLog(Long operId) {
        return operLogMapper.selectById(operId);
    }

    @Override
    public void deleteOperLog(Long operId) {
        int rows = operLogMapper.deleteById(operId);
        if (rows <= 0) {
            throw new ServiceException("删除操作日志失败，请联系工作人员");
        }
    }

    @Override
    public int insertOperLog(OperLog operLog) {
        return operLogMapper.insert(operLog);
    }

    /**
     * 清空操作日志
     */
    public void cleanOperLog() {
        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(OperLog::getOperId, 0);
        operLogMapper.delete(wrapper);
    }

    /**
     * 查询登录日志列表（支持按用户名、IP、状态筛选）
     */
    @Override
    public List<LoginInfo> listLoginInfo(LoginInfoSelectDTO dto) {
        LambdaQueryWrapper<LoginInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(dto.getUsername()), LoginInfo::getUsername, dto.getUsername())
                .like(StringUtils.isNotEmpty(dto.getIpAddr()), LoginInfo::getIpAddr, dto.getIpAddr())
                .eq(StringUtils.isNotEmpty(dto.getStatus()), LoginInfo::getStatus, dto.getStatus())
                .orderByDesc(LoginInfo::getInfoId);

        System.out.println("查询登录日志列表，查询条件：" + dto);
        System.out.println("\n" + wrapper);

        return loginInfoMapper.selectList(wrapper);
    }

    @Override
    public LoginInfo getLoginInfo(Long infoId) {
        return loginInfoMapper.selectById(infoId);
    }

    @Override
    public void deleteLoginInfo(Long infoId) {
        int rows = loginInfoMapper.deleteById(infoId);
        if (rows <= 0) {
            throw new ServiceException("删除登录日志失败，请联系工作人员");
        }
    }

    public int insertLoginInfo(LoginInfo loginInfo) {
        return loginInfoMapper.insert(loginInfo);
    }

    /**
     * 清空登录日志
     */
    public void cleanLoginInfo() {
        LambdaQueryWrapper<LoginInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(LoginInfo::getInfoId, 0);
        loginInfoMapper.delete(wrapper);
    }
}
