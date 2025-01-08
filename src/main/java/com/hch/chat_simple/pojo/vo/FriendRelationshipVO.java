package com.hch.chat_simple.pojo.vo;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 朋友关系表
 * </p>
 *
 * @author hch
 * @since 2024-12-06
 */
@Data
@Schema(name = "FriendRelationshipVO", description = "朋友关系表")
public class FriendRelationshipVO {


    @Schema(description = "id")
    private Long id;

    @Schema(description = "朋友id")
    private Long friendId;

    @Schema(description = "朋友名称")
    private String friendName;

    @Schema(description = "朋友备注")
    private String friendRemark;

    @Schema(description = "删除标记：0-未删除，1-已删除")
    private String dr;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "创建人")
    private String creatorBy;

    @Schema(description = "修改人id")
    private Long modifierId;

    @Schema(description = "修改人姓名")
    private String modifierBy;

    @Schema(description = "修改时间")
    private LocalDateTime updatedAt;

    @Schema(description = "创建人id")
    private Long creatorId;
}
