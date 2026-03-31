package com.sql.common.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StoreCreateDTO {
    @NotBlank(message = "店铺名称不能为空")
    private String storeName;

    private String address;
}
