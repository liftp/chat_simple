package com.hch.chat_simple.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Configuration
public class SnowflakeFactory {

    // @Autowired
    private StringRedisTemplate redisTemplate;

    // @Bean
    public SnowflakeIdGen snowflakeIdGen() {
        long workerId = redisTemplate.opsForValue().increment("snowflake:idGen:incr");
        log.info("This instance snowflake worderId is {}", workerId);
        return new SnowflakeIdGen(workerId);
    }
}
