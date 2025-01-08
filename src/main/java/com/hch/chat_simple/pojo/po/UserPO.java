package com.hch.chat_simple.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
// import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hch
 * @since 2024-11-06
 */
@Getter
@Setter
// @Schema(name = "User", description = "")
@TableName("user")
public class UserPO implements Serializable {

    private static final long serialVersionUID = 1L;

    //@Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //@Schema(description = "用户名")
    private String username;

    //@Schema(description = "密码")
    private String password;

    //@Schema(description = "真实姓名")
    private String name;

    //@Schema(description = "生日年月")
    // private LocalDateTime birthday;

    //@Schema(description = "创建日期")
    private LocalDateTime createdAt;

    //@Schema(description = "创建人id")
    private Long creatorId;

    //@Schema(description = "创建人姓名")
    private String creatorBy;

    //@Schema(description = "修改时间")
    private LocalDateTime updatedAt;

    //@Schema(description = "修改人id")
    private Long modifierId;

    //@Schema(description = "修改人姓名")
    private String modifierBy;

    //@Schema(description = "是否删除 0:未删除 1:已删除")
    @TableLogic
    private Integer dr;
}
