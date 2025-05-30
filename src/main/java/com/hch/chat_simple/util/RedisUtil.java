package com.hch.chat_simple.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
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

    public static long decrease(String key) {
        Long result = redisTemplate.opsForValue().decrement(key); 
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

    // 输入限制类型 Entry<K, V> 都是 String 类型
    public static void mapPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public static String mapGet(String key, String hashKey) {
        Object v = redisTemplate.opsForHash().get(key, hashKey);
        if (v == null) {
            return null;
        }
        return (String) v;
    }

    // 简单获取，Map<String, String>, 添加时限制了类型，Entry<K, V>都是String
    public static Map<String, String> mapGetAll(String key) {
        Map<Object, Object> objRes = redisTemplate.opsForHash().entries(key);
        Map<String, String> v = null;
        if (objRes != null) {
            v = new LinkedHashMap<>(objRes.size());
            for (Map.Entry<Object, Object> entry : objRes.entrySet()) {
                v.put((String)entry.getKey(), (String)entry.getValue());
            }
        }
        if (v == null) {
            return null;
        }
        return v;
    }

    public static Long mapPop(String key, String ...hashKey) {
        return redisTemplate.opsForHash().delete(key, hashKey);
    }



    
}
