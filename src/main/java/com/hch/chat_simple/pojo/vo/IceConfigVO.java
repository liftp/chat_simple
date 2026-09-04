package com.hch.chat_simple.pojo.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "IceConfigVO", description = "WebRTC ICE服务器配置")
public class IceConfigVO {

    @Schema(description = "ice urls")
    private List<String> urls;

    @Schema(description = "turn限时用户名 过期时间戳:用户id")
    private String username;

    @Schema(description = "turn凭证")
    private String credential;
}
