package com.hch.chat_simple.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

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
        String str = createToken("{\"username\": \"zhangsan\", \"userid\": \"1\"}");
        verigyToken(str);
    }

    public static DecodedJWT verigyToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(ENCRYPT_KEY))
                .withIssuer(ISSUER)
                .build();
            return jwtVerifier.verify(token);
        } catch (Exception e) {
            log.error("token verfier failed", e);
            return null;
        }
    }
}
