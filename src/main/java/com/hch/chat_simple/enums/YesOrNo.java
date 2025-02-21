package com.hch.chat_simple.enums;

import lombok.Getter;

@Getter
public enum YesOrNo {
    YES(1, "是"),
    NO(0, "是");
    YesOrNo(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    private Integer code;
    private String desc;
}
