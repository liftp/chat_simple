package com.hch.chat_simple.pojo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "FriendRelationshipQuery", description = "好友关系搜索")
public class FriendRelationshipQuery {
    
    @Schema(description = "名称模糊搜索")
    private String name;

    @Schema(description = "id精准搜索")
    private Long id;

    @Schema(description = "搜索类型 1-名称 2-id")
    private Integer searchType;
}
