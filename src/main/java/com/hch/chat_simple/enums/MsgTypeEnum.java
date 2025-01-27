package com.hch.chat_simple.enums;

import lombok.Getter;

@Getter
public enum MsgTypeEnum {
    UP_LINE(1, "上线"),
    SEND_MSG(2, "发送消息"),
    DOWN_LINE(3, "下线");
    private Integer type;
    private String name;
    MsgTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }


}
