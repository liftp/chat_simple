package com.hch.chat_simple.util;

import lombok.Getter;

@Getter
public enum StatusCodeEnum {
    SUCCESS(202, "成功"),
    TOKEN_INVALID(505, "token验证失败"),
    TOKEN_LACK(506, "token缺失"),
    TOKEN_EXPIRE(507, "token 已失效"),
    USER_NOT_FOUND(508, "用户不存在"),
    PWD_ERROR(509, "密码错误");
    private int code;
    private String desc;

    StatusCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
