package com.hch.chat_simple.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 群组成员
 * </p>
 *
 * @author hch
 * @since 2025-02-02
 */
@Data
@TableName("group_member")
@Schema(name = "GroupMemberVO", description = "群组成员")
public class GroupMemberVO {


    @Schema(description = "id")
    private Long id;

    @Schema(description = "群组id")
    private Long groupId;

    @Schema(description = "好友id")
    private Long memberId;

    @Schema(description = "好友名称")
    private String memberName;

    @Schema(description = "群内备注")
    private String memberRemark;

    @Schema(description = "邀请人")
    private Long inviteId;

}
