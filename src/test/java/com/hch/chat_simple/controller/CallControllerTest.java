package com.hch.chat_simple.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class CallControllerTest {

    @Test
    void turnCredentialIsHexHmacSha1() {
        // 向量独立计算: printf '1893456000:7' | openssl dgst -sha1 -hmac k
        assertEquals("924094f2449bdb504ea408d824e8a15ee8819262", CallController.sign("k", "1893456000:7"));
        assertEquals(40, CallController.sign("k", "1893456000:7").length());
        assertNotEquals(CallController.sign("k", "1893456000:7"), CallController.sign("k", "1893456000:8"));
    }
}
