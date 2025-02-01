package com.hch.chat_simple.pojo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ChatMsgDTO", description="单聊消息内容")
public class ChatMsgDTO {

    /**
     * @link MsgTypeEnum
     */
    @Schema(description = "消息类型")
    private Integer msgType;
    
    @Schema(description = "发送人")
    private Long sendUserId;

    @Schema(description = "接收人")
    private Long receiveUserId;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "群聊id")
    private Long groupId;

    @Schema(description = "服务器时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "时间戳字符串")
    private String dateTime;

}
