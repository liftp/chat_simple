package com.hch.chat_simple.listener;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class SpringContextListener {

    @Autowired
    private Environment environment;
    

    /**
     * 在上下文关闭后清理实例缓存信息
     * @param event
     */
    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        // 修改缓存保存的实例数量
        long num = RedisUtil.decrease(Constant.INST_NUM_KEY);
        log.info("服务实例数量：{}", num);
        String host = "";

        host = getHost();
        // String serverAddress = environment.getProperty("server.address");
        String serverPort = environment.getProperty("server.port");
        RedisUtil.mapPop(Constant.INST_WITH_MAP_KEY, host + ":" + serverPort);
    }
    

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
