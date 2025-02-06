package com.hch.chat_simple.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// @SuppressWarnings("unused")
public class RedisUtil implements ApplicationContextAware {

    private static StringRedisTemplate redisTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RedisUtil.redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
    }


    public static boolean delete(String key) {
        Boolean result = redisTemplate.delete(key);
        if (result == null) {
            throw new RuntimeException("redis delete result is null");
        }
        return result;
    }

    public static boolean hasKey(String key) {
        Boolean result = redisTemplate.hasKey(key);
        if (result == null) {
            throw new RuntimeException("redis hasKey result is null");
        }
        return result;
    }

    public static boolean expire(String key, long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.expire(key, timeout, unit);
        if (result == null) {
            throw new RuntimeException("redis expire result is null");
        }
        return result;
    }

    public static void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public static long incr(String key) {
        Long result = redisTemplate.opsForValue().increment(key); 
        if (result == null) {
            throw new RuntimeException("redis incrBy result is null");
        }
        return result;
    }

    public static long incrBy(String key, long incr) {
        Long result = redisTemplate.opsForValue().increment(key, incr); 
        if (result == null) {
            throw new RuntimeException("redis incrBy result is null");
        }
        return result;
    }
        
    public static String get(String key) {
        String result = redisTemplate.opsForValue().get(key);
        return result;
    }

    
}
