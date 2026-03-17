package com.sql.admin.service;

import java.util.List;

import com.sql.admin.dto.CourtCreateDTO;
import com.sql.admin.dto.CourtUpdateDTO;
import com.sql.common.entity.db.Court;

public interface CourtService {
    /**
     * 添加场地
     */
    int addCourt(CourtCreateDTO dto);

    /**
     * 更改场地信息
     */
    int updateCourt(Long courtId, CourtUpdateDTO dto);

    /**
     * 查询当前店铺的场地列表
     */
    List<Court> listCourts();

    /**
     * 根据ID查询场地
     */
    Court getCourtById(Long courtId);
}
