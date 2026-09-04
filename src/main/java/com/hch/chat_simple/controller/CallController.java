package com.hch.chat_simple.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hch.chat_simple.exception.BusinessException;
import com.hch.chat_simple.pojo.vo.IceConfigVO;
import com.hch.chat_simple.util.ContextUtil;
import com.hch.chat_simple.util.Payload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 语音通话：按 coturn restful api(use-auth-secret) 下发限时 TURN 凭证
 */
@RestController
@RequestMapping("/call")
@Tag(name = "语音通话")
public class CallController {

    @Value("${call.turn.urls:}")
    private String turnUrls;

    @Value("${call.turn.secret:}")
    private String turnSecret;

    @Value("${call.turn.ttl:3600}")
    private long ttl;

    @GetMapping("iceConfig")
    @Operation(description = "获取WebRTC ICE服务器配置")
    public Payload<IceConfigVO> iceConfig() {
        Long userId = ContextUtil.getUserId();
        // 未配置TURN时返回空配置，退化为内网直连
        if (userId == null || StringUtils.isBlank(turnUrls) || StringUtils.isBlank(turnSecret)) {
            return Payload.success(new IceConfigVO());
        }
        String username = (System.currentTimeMillis() / 1000 + ttl) + ":" + userId;
        return Payload.success(new IceConfigVO(List.of(turnUrls.split(",")), username, sign(turnSecret, username)));
    }

    /** coturn 共享密钥签名: hex(hmacSha1(secret, "过期时间戳:用户id")) */
    public static String sign(String secret, String username) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            StringBuilder hex = new StringBuilder();
            for (byte b : mac.doFinal(username.getBytes(StandardCharsets.UTF_8))) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new BusinessException("TURN凭证生成失败");
        }
    }
}
