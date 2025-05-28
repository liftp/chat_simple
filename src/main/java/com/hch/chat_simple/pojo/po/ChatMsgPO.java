package com.hch.chat_simple.pojo.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("chat_msg")
@Schema(name = "ChatMsgPO", description="聊天消息内容")
public class ChatMsgPO extends BasePO {

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * @link MsgTypeEnum
     */
    @Schema(description = "消息类型")
    private Integer msgType;

    @Schema(description = "是否群聊 1:单聊 2:群聊")
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


}
