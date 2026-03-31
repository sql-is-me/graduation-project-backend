package com.sql.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sql.common.entity.po.Store;

@Mapper
public interface StoreMapper extends BaseMapper<Store> {
    @Select("SELECT * FROM stores WHERE store_name = #{storeName}")
    Store selectByStoreName(@Param("storeName") String storeName);

    @Update("UPDATE stores SET status = #{status} WHERE store_id = #{storeId}")
    int updateStatus(@Param("storeId") Long storeId, @Param("status") String status);

    @Update("UPDATE stores SET owner_id = #{ownerId} WHERE store_id = #{storeId}")
    int updateOwnerId(@Param("storeId") Long storeId, @Param("ownerId") Long ownerId);
}
