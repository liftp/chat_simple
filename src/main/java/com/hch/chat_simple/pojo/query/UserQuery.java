package com.hch.chat_simple.pojo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "UserQuery", description = "用户查询")
public class UserQuery {
    
    @Schema(description = "用户账号")
    @NotBlank(message = "用户账户不能为空")
    private String username;
}
