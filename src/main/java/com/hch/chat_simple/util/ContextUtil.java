package com.hch.chat_simple.util;

import com.alibaba.ttl.TransmittableThreadLocal;

public class ContextUtil {
    // 登录名
    private static ThreadLocal<String> usernameHolder = new TransmittableThreadLocal<>();
    private static ThreadLocal<String> realNameHolder = new TransmittableThreadLocal<>();
    private static ThreadLocal<Long> userIdHolder = new TransmittableThreadLocal<>();

    public static void setUserId(Long userId) {
        ContextUtil.userIdHolder.set(userId);
    }

    public static Long getUserId() {
        return userIdHolder.get();
    }

    public static void setUsername(String username) {
        ContextUtil.usernameHolder.set(username);
    }

    public static String getUsername() {
        return usernameHolder.get();
    }

    public static void setRealName(String realName) {
        ContextUtil.realNameHolder.set(realName);
    }

    public static String getRealName() {
        return realNameHolder.get();
    }

    public static void clear() {
        userIdHolder.set(null);
        usernameHolder.set(null);
        realNameHolder.set(null);
    }

}
