package com.hch.chat_simple.pojo.dto;

import lombok.Data;

/**
 * 好友在线状态通知 DTO
 * 当用户上线/下线时，通过 composition topic 推送给其好友
 */
@Data
public class OnlineStatusDTO {
    /** 状态变更的用户id */
    private Long userId;
    /** 用户名 */
    private String username;
}
