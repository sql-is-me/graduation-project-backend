package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Store;
import com.sql.common.entity.vo.StoreInfo;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {
    @Select("SELECT * FROM stores WHERE store_name = #{storeName}")
    Store selectByStoreName(@Param("storeName") String storeName);

    @Select("SELECT s.store_id, s.creator_id, s.store_name, s.address, s.owner_id, s.status, s.create_time, s.update_time, " +
            "c.nick_name AS creatorName, o.nick_name AS ownerName " +
            "FROM stores s " +
            "LEFT JOIN admins c ON s.creator_id = c.admin_id " +
            "LEFT JOIN admins o ON s.owner_id = o.admin_id " +
            "WHERE s.store_id = #{storeId}")
    StoreInfo selectStoreInfoById(@Param("storeId") Long storeId);

    @Update("UPDATE stores SET status = #{status} WHERE store_id = #{storeId}")
    int updateStatus(@Param("storeId") Long storeId, @Param("status") String status);

    @Update("UPDATE stores SET owner_id = #{ownerId} WHERE store_id = #{storeId}")
    int updateOwnerId(@Param("storeId") Long storeId, @Param("ownerId") Long ownerId);
}
