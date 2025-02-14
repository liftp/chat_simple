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
 * 好友申请记录
 * </p>
 *
 * @author hch
 * @since 2025-02-14
 */
@Data
@TableName("apply_friend")
@Schema(name = "ApplyFriendPO", description = "好友申请记录")
public class ApplyFriendPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @Schema(description = "申请状态： 0:申请中 1:通过 2:拒绝")
    private Integer applyStatus;

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
