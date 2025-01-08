package com.hch.chat_simple.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CrossInterceptorHandler implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 设置响应头，允许来自https://example.com的跨域请求
        response.setHeader("Access-Control-Allow-Origin", "https://chat.front.com");
        // 允许浏览器发送cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 允许的HTTP方法
        response.setHeader("Access-Control-Allow-Methods", "POST, GET , PUT , OPTIONS");
        // 预检请求的缓存时间
        response.setHeader("Access-Control-Max-Age", "3600");
        // 允许的请求头
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,accept,authorization,content-type");
        return true;
    }
    
}
