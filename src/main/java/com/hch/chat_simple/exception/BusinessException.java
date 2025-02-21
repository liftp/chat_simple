package com.hch.chat_simple.exception;

public class BusinessException extends RuntimeException {
    
    public BusinessException() {

    }
    public BusinessException(String msg) {
        super(msg);
    }
}
