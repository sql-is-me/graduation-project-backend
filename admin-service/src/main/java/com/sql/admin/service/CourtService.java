package com.sql.admin.service;

import java.util.List;

import com.sql.admin.dto.CourtCreateDTO;
import com.sql.admin.dto.CourtUpdateDTO;
import com.sql.common.entity.db.Court;

public interface CourtService {
    /**
     * 添加场地
     */
    long addCourt(CourtCreateDTO dto);

    /**
     * 更改场地信息
     */
    void updateCourt(Long courtId, CourtUpdateDTO dto);

    /**
     * 删除场地
     */
    void deleteCourt(Long courtId);

    /**
     * 查询当前店铺的场地列表
     */
    List<Court> listCourts(Long storeId);

    /**
     * 根据ID查询场地
     */
    Court getCourtById(Long courtId);
}
