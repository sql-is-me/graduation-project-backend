package com.sql.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sql.admin.dto.CourtCreateDTO;
import com.sql.admin.dto.CourtUpdateDTO;
import com.sql.admin.mapper.CourtMapper;
import com.sql.admin.service.CourtService;
import com.sql.common.entity.db.Court;
import com.sql.common.exception.ServiceException;
import com.sql.common.header.ContextHolder;
import com.sql.utils.StringUtils;

@Service
public class CourtServiceImpl implements CourtService {

    @Autowired
    private CourtMapper courtMapper;

    @Override
    public long addCourt(CourtCreateDTO dto) {
        Long storeId = getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }

        // 检查同店铺下场地名称是否重复
        LambdaQueryWrapper<Court> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Court::getStoreId, storeId)
                .eq(Court::getCourtName, dto.getCourtName());
        if (courtMapper.selectCount(wrapper) > 0) {
            throw new ServiceException("场地名称已存在");
        }

        Court court = new Court();
        court.setStoreId(storeId);
        court.setCourtName(dto.getCourtName());

        int rows = courtMapper.insert(court);
        if (rows <= 0) {
            throw new ServiceException("创建场地失败");
        }

        return court.getCourtId();
    }

    @Override
    public void updateCourt(Long courtId, CourtUpdateDTO dto) {
        Long storeId = getStoreId();
        Court court = courtMapper.selectById(courtId);
        if (court == null) {
            throw new ServiceException("场地不存在");
        }
        if (!court.getStoreId().equals(storeId)) {
            throw new ServiceException("无权操作其他店铺的场地");
        }

        // 如果修改名称，检查重复
        if (StringUtils.isNotEmpty(dto.getCourtName()) && !dto.getCourtName().equals(court.getCourtName())) {
            LambdaQueryWrapper<Court> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Court::getStoreId, storeId)
                    .eq(Court::getCourtName, dto.getCourtName());
            if (courtMapper.selectCount(wrapper) > 0) {
                throw new ServiceException("场地名称已存在");
            }
            court.setCourtName(dto.getCourtName());
        }

        if (StringUtils.isNotEmpty(dto.getStatus())) {
            if (!"0".equals(dto.getStatus()) && !"1".equals(dto.getStatus())) {
                throw new ServiceException("状态值无效，仅支持0(正常)和1(维护中)");
            }
            court.setStatus(dto.getStatus());
        }

        int rows = courtMapper.updateById(court);
        if (rows <= 0) {
            throw new ServiceException("更新场地信息失败");
        }
    }

    @Override
    public void deleteCourt(Long courtId) {
        int rows = courtMapper.deleteById(courtId);
        if (rows <= 0) {
            throw new ServiceException("删除场地失败");
        }
    }

    @Override
    public List<Court> listCourts(Long storeId) {
        // 店铺管理员查询的话是null，需要获取
        if (storeId == null) {
            storeId = getStoreId();
        }

        LambdaQueryWrapper<Court> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Court::getStoreId, storeId)
                .orderByAsc(Court::getCourtId);
        return courtMapper.selectList(wrapper);
    }

    @Override
    public Court getCourtById(Long courtId) {
        Long storeId = getStoreId();
        Court court = courtMapper.selectById(courtId);
        if (court == null) {
            throw new ServiceException("场地不存在");
        }
        if (!court.getStoreId().equals(storeId)) {
            throw new ServiceException("无权查看其他店铺的场地");
        }
        return court;
    }

    private Long getStoreId() {
        Long storeId = ContextHolder.getAO().getAdminInfo().getStoreId();
        if (storeId == null) {
            throw new ServiceException("当前管理员未绑定店铺");
        }
        return storeId;
    }
}
