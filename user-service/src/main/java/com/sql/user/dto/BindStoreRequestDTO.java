package com.sql.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BindStoreRequestDTO {

    @NotBlank(message = "邀请码不能为空")
    private String inviteCode;

    private String message;
}
