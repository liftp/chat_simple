package com.hch.chat_simple.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name="GroupInfoVO", description = "群组信息VO")
public class GroupInfoVO {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "群组名称")
    private String groupName;

    @Schema(description = "群组信息")
    private String groupRemark;

    @Schema(description = "群状态： 0:开放 1:成员邀请加入 2:仅所属人拉取 3:密码进入")
    private Integer groupStatus;

    @Schema(description = "群进入密码")
    private String groupLockPwd;

    @Schema(description = "群组所属人id")
    private Long selfId;
}
