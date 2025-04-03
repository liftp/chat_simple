package com.hch.chat_simple.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 朋友关系表
 * </p>
 *
 * @author hch
 * @since 2024-12-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("friend_relationship")
@Schema(name = "FriendRelationshipPO", description = "朋友关系表")
public class FriendRelationshipPO extends BasePO {


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

}
