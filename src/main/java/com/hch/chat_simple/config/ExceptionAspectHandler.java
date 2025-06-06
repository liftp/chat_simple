package com.hch.chat_simple.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hch.chat_simple.util.ContextUtil;
import com.hch.chat_simple.util.Payload;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExceptionAspectHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public Payload<?> handleTokenExpireException(TokenExpiredException e) {
        log.error("token was expired", e);
        return Payload.of(ContextUtil.getNewToken(), 507, "token 过期");
    }
    

}
