package com.hch.chat_simple.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WebSocketPerssionVerify {
    
    @Schema(name = "token")
    private String token;

    @Schema(name = "用户id")
    private Long userId;

}
