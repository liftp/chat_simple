package com.hch.chat_simple.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 群组信息
 * </p>
 *
 * @author hch
 * @since 2025-02-02
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@TableName("group_info")
@Schema(name = "GroupInfoPO", description = "群组信息")
public class GroupInfoPO extends BasePO {


    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
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
