package com.hch.chat_simple.pojo.vo;

import java.time.LocalDateTime;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ChatMsgVO", description="聊天消息内容")
public class ChatMsgVO {

    @Schema(description = "id")
    private Long msgId;
    /**
     * @link MsgTypeEnum
     */
    @Schema(description = "消息类型")
    private Integer msgType;

    @Schema(description = "是否群聊 0:单聊 1:群聊")
    private Integer chatType;
    
    @Schema(description = "发送人")
    private Long sendUserId;

    @Schema(description = "接收人，群聊时为空")
    private Long receiveUserId;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "群聊id")
    private Long groupId;

    @Schema(description = "消息发送状态 0:失败 1:成功")
    private Integer status;

    @Schema(description = "删除标记：0-未删除，1-已删除")
    private String dr;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "创建人id")
    private Long creatorId;

    @Schema(description = "根据创建时间生成的时间戳")
    private Long dateTime;

    @Schema(description = "消息内容类型 1: 文本 2: 语音")
    private Integer contentType;

    @Schema(description = "内容长度")
    private Integer contentLen;

}
