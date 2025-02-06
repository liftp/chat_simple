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
public class GroupMemberPO implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "创建人id")
    private Long creatorId;

    @Schema(description = "创建人姓名")
    private String creatorBy;

    @Schema(description = "修改时间")
    private LocalDateTime updatedAt;

    @Schema(description = "修改人id")
    private Long modifierId;

    @Schema(description = "修改人姓名")
    private String modifierBy;

    @Schema(description = "是否删除 0:未删除 1:已删除")
    @TableLogic
    private Integer dr;
}
