package com.hch.chat_simple.pojo.po;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "BasePO", description = "基础PO字段")
public class BasePO {
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @Schema(description = "创建人id")
    @TableField(fill = FieldFill.INSERT)
    private Long creatorId;

    @Schema(description = "创建人姓名")
    @TableField(fill = FieldFill.INSERT)
    private String creatorBy;

    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    @Schema(description = "修改人id")
    @TableField(fill = FieldFill.UPDATE)
    private Long modifierId;

    @Schema(description = "修改人姓名")
    @TableField(fill = FieldFill.UPDATE)
    private String modifierBy;

    @Schema(description = "是否删除 0:未删除 1:已删除")
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer dr;
}
