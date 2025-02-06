package com.hch.chat_simple.pojo.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hch.chat_simple.pojo.vo.UserVO;

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

    @Schema(description = "聊天类型 1:单聊 2:群聊")
    private Integer chatType;
    
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

    // @Schema(description = "发送人信息")
    // private UserVO sendUser;

    @Schema(description = "消息id,群聊使用")
    private Long msgId;

    @Schema(description = "好友id,关联前端的好友列表数据,单聊为单聊用户ID,群聊为群组id")
    private Long friendId;

}
