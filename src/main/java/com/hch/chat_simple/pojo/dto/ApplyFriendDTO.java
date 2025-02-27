package com.hch.chat_simple.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "ApplyFriendDTO", description = "申请好友DTO")
public class ApplyFriendDTO {
    

    @Schema(description = "申请人id")
    private Long proposerId;

    @Schema(description = "申请人名称")
    private String proposerName;

    @Schema(description = "申请人备注")
    @NotBlank(message = "本人描述不能为空")
    private String proposerRemark;

    @Schema(description = "申请理由")
    private String proposerReason;

    @Schema(description = "被申请好友id")
    @NotNull(message = "添加好友的id不能为空")
    private Long targetUser;

    // 备注区用于好友列表展示
    @Schema(description = "申请给被申请人的备注")
    private String appliedRemark;

    @Schema(description = "被申请给申请人的备注")
    private String applyRemark;

    // 申请结果
    @Schema(description = "申请是否通过: 0-拒绝 1-成功")
    private Integer applyPass;
}
