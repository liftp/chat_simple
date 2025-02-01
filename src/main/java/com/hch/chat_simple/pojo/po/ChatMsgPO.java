package com.hch.chat_simple.pojo.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("chat_msg")
@Schema(name = "ChatMsgPO", description="聊天消息内容")
public class ChatMsgPO {

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * @link MsgTypeEnum
     */
    @Schema(description = "消息类型")
    private Integer msgType;

    @Schema(description = "是否群聊 0:单聊 1:群聊")
    private Integer groupType;
    
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
    @TableLogic
    private String dr;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "创建人")
    private String creatorBy;

    @Schema(description = "修改人id")
    private Long modifierId;

    @Schema(description = "修改人姓名")
    private String modifierBy;

    @Schema(description = "修改时间")
    private LocalDateTime updatedAt;

    @Schema(description = "创建人id")
    private Long creatorId;

}
