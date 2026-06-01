package com.hch.chat_simple.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hch.chat_simple.pojo.dto.TokenPairDTO;
import com.hch.chat_simple.util.ContextUtil;
import com.hch.chat_simple.util.Payload;
import com.hch.chat_simple.util.StatusCodeEnum;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExceptionAspectHandler {

    /**
     * 处理AccessToken过期异常
     * 当LoginInterceptor通过RefreshToken成功刷新AccessToken后，抛出此异常
     * 返回新的AccessToken给客户端，客户端需更新本地存储的AccessToken
     */
    @ExceptionHandler(TokenExpiredException.class)
    public Payload<TokenPairDTO> handleTokenExpireException(TokenExpiredException e) {
        log.warn("accessToken已过期并刷新: {}", e.getMessage());
        String newAccessToken = ContextUtil.getNewAccessToken();
        TokenPairDTO tokenPair = TokenPairDTO.builder()
                .accessToken(newAccessToken)
                .build();
        return Payload.of(tokenPair, StatusCodeEnum.TOKEN_EXPIRE.getCode(), "accessToken已刷新");
    }

}
