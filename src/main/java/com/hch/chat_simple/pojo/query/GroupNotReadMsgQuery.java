package com.hch.chat_simple.pojo.query;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "GroupNotReadMsgQuery", description = "群聊查询未读消息")
public class GroupNotReadMsgQuery {
    

    @Schema(description = "查询的群聊列表")
    private List<SingleGroupParam> groupList;

    @Data
    @Schema(name = "SingleGroupParam", description = "单条群聊的参数")
    public static class SingleGroupParam {
    
        @Schema(description = "群聊id")
        private Long groupId;
        @Schema(description = "最后一条消息id")
        private Long msgId;
        
    }
}
