package com.hch.chat_simple.enums;

import lombok.Getter;

@Getter
public enum MsgTypeEnum {
    UP_LINE(1, "上线"),
    SEND_MSG(2, "聊天消息"),
    DOWN_LINE(3, "下线"),
    APPLY_FRIEND(4, "申请好友"),
    APPLY_FRIEND_RESULT(5, "申请好友结果");
    private Integer type;
    private String name;
    MsgTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }


}
