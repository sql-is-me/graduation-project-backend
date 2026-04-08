package com.sql.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.bo.CoachBriefInfo;
import com.sql.common.entity.bo.ManagerBriefInfo;
import com.sql.common.entity.bo.StoreBriefInfo;
import com.sql.common.entity.po.Store;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {
    /**
     * 查询店铺基本信息
     */
    @Select("SELECT store_id AS storeId, store_name AS storeName, address FROM stores WHERE store_id = #{storeId}")
    StoreBriefInfo selectStoreBriefById(@Param("storeId") Long storeId);

    /**
     * 查询店铺下所有管理员的简要信息
     */
    @Select("SELECT nick_name AS nickName, email, phone, sex, avatar FROM admins " +
            "WHERE store_id = #{storeId} AND status = '0'")
    List<ManagerBriefInfo> selectManagersByStoreId(@Param("storeId") Long storeId);

    /**
     * 查询店铺下所有教练的简要信息
     */
    @Select("SELECT nick_name AS nickName, sex, avatar, photo FROM users " +
            "WHERE store_id = #{storeId} AND user_type = '1' AND status = '0'")
    List<CoachBriefInfo> selectCoachesByStoreId(@Param("storeId") Long storeId);

    /**
     * 模糊搜索店铺名称（仅返回正常营业的店铺）
     */
    @Select("SELECT store_id AS storeId, store_name AS storeName, address FROM stores " +
            "WHERE store_name LIKE CONCAT('%', #{keyword}, '%') AND status = '0' ORDER BY store_id ASC")
    List<StoreBriefInfo> searchByName(@Param("keyword") String keyword);
}
