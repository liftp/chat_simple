package com.hch.chat_simple.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;
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
    private final static String ENCRYPT_KEY = "testabcd";
    private final static String ISSUER = "chat_admin";

    /** AccessToken 有效期：30分钟 */
    public static final int ACCESS_TOKEN_EXPIRE_MINUTES = 30;
    /** RefreshToken 有效期：7天 */
    public static final int REFRESH_TOKEN_EXPIRE_DAYS = 7;

    /** Redis key 前缀：AccessToken */
    public static final String REDIS_ACCESS_TOKEN_PREFIX = "accessToken:";
    /** Redis key 前缀：用户与AccessToken的映射 */
    public static final String REDIS_USER_ACCESS_TOKEN_PREFIX = "userAccessToken:";

    // ==================== AccessToken (存储在Redis中，短期有效) ====================

    /**
     * 创建AccessToken (UUID随机字符串，存储于Redis)
     * @return AccessToken字符串
     */
    public static String createAccessToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 将AccessToken存入Redis
     * @param accessToken AccessToken字符串
     * @param tokenInfo 用户信息
     */
    public static void storeAccessToken(String accessToken, TokenInfoDTO tokenInfo) {
        String tokenInfoJson = JSON.toJSONString(tokenInfo);
        RedisUtil.set(REDIS_ACCESS_TOKEN_PREFIX + accessToken, tokenInfoJson);
        RedisUtil.expire(REDIS_ACCESS_TOKEN_PREFIX + accessToken, ACCESS_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
        // 维护 userId -> accessToken 的映射，方便按用户维度使token失效
        RedisUtil.set(REDIS_USER_ACCESS_TOKEN_PREFIX + tokenInfo.getUserId(), accessToken);
        RedisUtil.expire(REDIS_USER_ACCESS_TOKEN_PREFIX + tokenInfo.getUserId(), ACCESS_TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 从Redis中获取AccessToken对应的用户信息
     * @param accessToken AccessToken字符串
     * @return TokenInfoDTO，如果token无效或已过期返回null
     */
    public static TokenInfoDTO getAccessTokenInfo(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            return null;
        }
        String tokenInfoJson = RedisUtil.get(REDIS_ACCESS_TOKEN_PREFIX + accessToken);
        if (tokenInfoJson == null) {
            return null;
        }
        return JSON.parseObject(tokenInfoJson, TokenInfoDTO.class);
    }

    /**
     * 删除AccessToken（用于登出或使token失效）
     * @param accessToken AccessToken字符串
     */
    public static void removeAccessToken(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            TokenInfoDTO info = getAccessTokenInfo(accessToken);
            RedisUtil.delete(REDIS_ACCESS_TOKEN_PREFIX + accessToken);
            if (info != null) {
                RedisUtil.delete(REDIS_USER_ACCESS_TOKEN_PREFIX + info.getUserId());
            }
        }
    }

    /**
     * 根据userId使AccessToken失效
     * @param userId 用户ID
     */
    public static void removeAccessTokenByUserId(Long userId) {
        if (userId != null) {
            String accessToken = RedisUtil.get(REDIS_USER_ACCESS_TOKEN_PREFIX + userId);
            if (accessToken != null) {
                RedisUtil.delete(REDIS_ACCESS_TOKEN_PREFIX + accessToken);
            }
            RedisUtil.delete(REDIS_USER_ACCESS_TOKEN_PREFIX + userId);
        }
    }

    // ==================== RefreshToken (JWT，7天有效，存放浏览器端) ====================

    /**
     * 创建RefreshToken (JWT，7天有效期)
     * @param jsonString 用户信息JSON字符串
     * @return RefreshToken JWT字符串
     */
    public static String createRefreshToken(String jsonString) {
        return JWT.create()
            .withSubject(jsonString)
            .withIssuer(ISSUER)
            .withExpiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_EXPIRE_DAYS).toInstant(ZoneOffset.ofHours(8)))
            .withClaim("type", "refresh")
            .sign(Algorithm.HMAC256(ENCRYPT_KEY));
    }

    /**
     * 验证RefreshToken，严格校验过期时间
     * @param token RefreshToken字符串
     * @return DecodedJWT，验证失败返回null
     */
    public static DecodedJWT verifyRefreshToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(ENCRYPT_KEY))
                .withIssuer(ISSUER)
                .build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            // 严格校验：如果RefreshToken已过期则返回null
            Date expiresAt = decodedJWT.getExpiresAt();
            if (expiresAt != null && expiresAt.before(new Date())) {
                return null;
            }
            return decodedJWT;
        } catch (Exception e) {
            log.error("refreshToken verify failed", e);
            return null;
        }
    }

    /**
     * 从RefreshToken中解析用户信息
     * @param token RefreshToken字符串
     * @return TokenInfoDTO，解析失败返回null
     */
    public static TokenInfoDTO parseRefreshTokenInfo(String token) {
        DecodedJWT decodedJWT = verifyRefreshToken(token);
        if (decodedJWT != null) {
            String subject = decodedJWT.getSubject();
            return JSON.parseObject(subject, TokenInfoDTO.class);
        }
        return null;
    }

    // ==================== 兼容旧Token（用于WebSocket等场景） ====================

    /**
     * @deprecated 旧版单Token机制，仅用于兼容，请使用 createRefreshToken
     */
    @Deprecated
    public static String createToken(String jsonString) {
        return JWT.create()
            .withSubject(jsonString)
            .withIssuer(ISSUER)
            .withExpiresAt(LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTES).toInstant(ZoneOffset.ofHours(8)))
            .withClaim("test", "123")
            .sign(Algorithm.HMAC256(ENCRYPT_KEY));
    }

    /**
     * @deprecated 旧版单Token验证，仅用于兼容
     */
    @Deprecated
    public static DecodedJWT verifyToken(String token) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(ENCRYPT_KEY))
                .acceptExpiresAt(ACCESS_TOKEN_EXPIRE_MINUTES * 60)
                .withIssuer(ISSUER)
                .build();
            return jwtVerifier.verify(token);
        } catch (Exception e) {
            log.error("token verfier failed", e);
            return null;
        }
    }

    /**
     * @deprecated 旧版解析Token信息，仅用于兼容
     */
    @Deprecated
    public static TokenInfoDTO parseTokenInfo(String token) {
        DecodedJWT decodeJwt = verifyToken(token);
        if (decodeJwt != null) {
            String subject = decodeJwt.getSubject();
            return JSON.parseObject(subject, TokenInfoDTO.class);
        }
        return null;
    }
}
