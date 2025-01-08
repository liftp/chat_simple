package com.hch.chat_simple.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.annotations.ApiModel;
// import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "UserLoginDTO", description = "用户登录DTO")
public class UserLoginDTO {

    @Schema(name = "username", description = "登录名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(name = "password", description = "密码")
    @NotBlank(message = "登录密码不能为空")
    private String password;
    
}
