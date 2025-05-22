package com.hch.chat_simple.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
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
@Schema(name = "GroupMemberPO", description = "群组成员")
public class GroupMemberPO extends BasePO {


    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
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

    @Schema(description = "成员群组中状态 0: 在群聊中 1: 离开群聊，默认1")
    private Integer status;

}
