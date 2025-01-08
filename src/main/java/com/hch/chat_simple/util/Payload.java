package com.hch.chat_simple.util;

import lombok.Data;

@Data
public class Payload<T> {
    
    public T data;
    public int code;
    public String remark;

    public Payload(T data, int code, String remark) {
        this.data = data;
        this.code = code;
        this.remark = remark;
    }

    public static <T> Payload<T> of(T data, int code, String remark) {
        return new Payload<>(data, code, remark);
    }

    public static <T> Payload<T> of(T data, StatusCodeEnum codeEnum) {
        return new Payload<T>(data, codeEnum.getCode(), codeEnum.getDesc());
    }

    public static <T> Payload<T> success(T data) {
        return new Payload<T>(data, StatusCodeEnum.SUCCESS.getCode(), StatusCodeEnum.SUCCESS.getDesc());
    }
}
