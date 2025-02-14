package com.hch.chat_simple.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ApplyFriendDTO", description = "申请好友DTO")
public class ApplyFriendDTO {
    

    @Schema(description = "申请人id")
    private Long proposerId;

    @Schema(description = "申请人名称")
    private String proposerName;

    @Schema(description = "申请人备注")
    private String proposerRemark;

    @Schema(description = "申请理由")
    private String proposerReason;

    @Schema(description = "被申请好友id")
    private Long targetUser;
}
