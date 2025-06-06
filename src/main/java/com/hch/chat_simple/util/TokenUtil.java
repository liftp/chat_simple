package com.hch.chat_simple.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hch.chat_simple.pojo.dto.TokenInfoDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenUtil {
    private final static String ENCRYPT_KEY =  "testabcd";
    private final static int EXPIRE_TIME =  30;
    private final static String ISSUER =  "chat_admin";

    public static String createToken(String jsonString) {
        return JWT.create()
            .withSubject(jsonString)
            .withIssuer(ISSUER)
            .withExpiresAt(LocalDateTime.now().plusMinutes(EXPIRE_TIME).toInstant(ZoneOffset.ofHours(8)))
            .withClaim("test", "123")
            .sign(Algorithm.HMAC256(ENCRYPT_KEY));
    }

    public static void main(String[] args) {
    //     String str = createToken("{\"username\": \"zs\", \"userid\": \"1\"}");
    //    DecodedJWT verifyToken = verifyToken(str);
    //    System.out.println(verifyToken.getExpiresAt());

        TokenInfoDTO tokeInfo = TokenInfoDTO.builder()
            .username("zs")
            .userId(6L)
            .realName("张三")
            .build();

        String tokenGen = JSON.toJSONString(tokeInfo);
        String continueToken = TokenUtil.createToken(tokenGen);
        System.out.println(continueToken);
    }

    public static DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(ENCRYPT_KEY))
                .acceptExpiresAt(EXPIRE_TIME * 60) // 失效的可保持30分钟，用于拿到用户信息，但是后续会补充抛出异常，并返回新的token，续接
                .withIssuer(ISSUER)
                .build();
            return jwtVerifier.verify(token);
        } catch (Exception e) {
            log.error("token verfier failed", e);
            return null;
        }
    }

    public static TokenInfoDTO parseTokenInfo(String token) {
        DecodedJWT decodeJwt = verifyToken(token);
        if (decodeJwt != null) {
            String subject = decodeJwt.getSubject();
            return JSON.parseObject(subject, TokenInfoDTO.class);
        }
        return null;

    }
}
