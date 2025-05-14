package com.hch.chat_simple.pojo.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "AddGroupMembersDTO", description = "添加群聊成员DTO")
public class AddGroupMembersDTO {
    @Schema(description = "用户ids")
    private List<Long> userIds;
    @Schema(description = "群聊id")
    private Long groupId;
}
