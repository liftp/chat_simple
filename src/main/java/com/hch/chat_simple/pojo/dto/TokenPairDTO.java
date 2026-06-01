package com.hch.chat_simple.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 双Token响应DTO
 * accessToken: 短期访问令牌(30分钟)，存储在Redis中
 * refreshToken: 长期刷新令牌(7天)，JWT存放在浏览器端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPairDTO {

    /** 短期访问令牌，30分钟有效，存储在Redis中 */
    private String accessToken;

    /** 长期刷新令牌，7天有效，JWT存放在浏览器端 */
    private String refreshToken;
}
