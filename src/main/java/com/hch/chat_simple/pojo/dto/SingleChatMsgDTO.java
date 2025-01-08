package com.hch.chat_simple.pojo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "SingleChatMsgDTO", description="单聊消息内容")
public class SingleChatMsgDTO {
    
    @Schema(description = "发送人")
    private Long from;

    @Schema(description = "接收人")
    private Long to;

    @Schema(description = "消息内容")
    private String msg;

    @Schema(description = "服务器时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime serverDate;

}
