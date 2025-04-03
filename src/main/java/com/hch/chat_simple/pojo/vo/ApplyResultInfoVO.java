package com.hch.chat_simple.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ApplyResultInfoVO", description = "申请结果响应")
public class ApplyResultInfoVO {
    
    @Schema(description = "申请状态： 0:申请中 1:通过 2:拒绝")
    private Integer applyStatus;

    @Schema(description = "申请人id")
    private Long proposerId;

    @Schema(description = "申请人备注")
    private String proposerRemark;

    @Schema(description = "被申请好友id")
    private Long targetUser;

    @Schema(description = "申请人的好友关系id")
    private Long proposerRelationshipId;

    @Schema(description = "被申请人的好友关系id")
    private Long targetRelationshipId;
}
