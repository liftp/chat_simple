package com.hch.chat_simple.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
// import lombok.experimental.Accessors;

@Data
// @Accessors(chain = true, fluent = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoDTO {
    
    private String username;
    private String realName;
    private Long userId;
}
