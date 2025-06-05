package com.hch.chat_simple.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MqProducerConfig {
    

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Value("${rocketmq.name-server}")
    private String namerServer;
    
    @Bean
    public DefaultMQProducer mqPproducer() throws MQClientException {
         DefaultMQProducer singleProducer = new DefaultMQProducer(producerGroup);
         singleProducer.setNamesrvAddr(namerServer);
         singleProducer.start();
         return singleProducer;
    }

}
