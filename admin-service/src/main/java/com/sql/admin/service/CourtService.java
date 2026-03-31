package com.sql.admin.service;

import java.util.List;

import com.sql.common.entity.dto.CourtCreateDTO;
import com.sql.common.entity.dto.CourtUpdateDTO;
import com.sql.common.entity.po.Court;
import com.sql.common.entity.vo.CourtInfo;

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
    List<CourtInfo> listCourts(Long storeId);

    /**
     * 根据ID查询场地
     */
    Court getCourtById(Long courtId);
}
