package com.hch.chat_simple.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 朋友关系表
 * </p>
 *
 * @author hch
 * @since 2024-12-06
 */
@Data
@TableName("friend_relationship")
@Schema(name = "FriendRelationshipPO", description = "朋友关系表")
public class FriendRelationshipPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "朋友id")
    private Long friendId;

    @Schema(description = "朋友名称")
    private String friendName;

    @Schema(description = "朋友备注")
    private String friendRemark;
    
    @Schema(description = "好友关系所属id")
    private Long selfId;

    @Schema(description = "删除标记：0-未删除，1-已删除")
    @TableLogic
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
