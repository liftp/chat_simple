package com.hch.chat_simple.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@AutoConfigureAfter(RedisUtil.class)
public class InstanceCreateHook {
    
    @Autowired
    private Environment environment;

    
    public static class InnerInstanceCreateHook {
    
        
    }

    
    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 在redis工具类注入成功后，保存实例和redis中实例数量对应的映射，这样，类似实例A-1映射保存，实例B-2映射保存，..., 然后实例全部加载完之后，实例的数量就是取模的因子，
     * 在使用的时候，只需要根据发送的用户id取模实例数量（本项目使用数据库自增，所以是Long值），然后根据实例名与映射数，直接hash到对应的实例上，
     * 项目使用spring的请求转发到不同目标用户的操作，
     * 1. 像单聊的情况，这个用户就是消息接收人， 由于建立netty+ws会话的时候，也是将会话人映射到对应的实例上，
     * 然后再次根据消息接收人映射，就能直接命中多实例中存有该用户的实例（有可能用户没登录，没有会话，但是有相关的消息存储处理），这样只是多了一次转发功能
     * 2. 像群聊的情况，这里显示获取群聊的所有用户，将所有用户逐个取模，找到不同的实例，然后group by所有实例，再去将接收人放到header上，这样转发后，不同实例
     * 分别处理当前实例上映射分得的用户
     * 
     * @param event
     */
    @Bean
    public InnerInstanceCreateHook instanceHook() {
        String host = "";

        host = getHost();
        // String serverAddress = environment.getProperty("server.address");
        String serverPort = environment.getProperty("server.port");
        log.info("实例ip:{}, 端口:{}", host, serverPort);
        // 增加缓存的实例数量
        long num = RedisUtil.incr(Constant.INST_NUM_KEY);
        log.info("服务实例数量增加：{}", num);
        RedisUtil.mapPut(Constant.INST_WITH_MAP_KEY, host + ":" + serverPort,  num + "");
        log.info("实例ip:{}, 端口:{}", host, serverPort);
        return new InnerInstanceCreateHook();
    }
}
