package com.hch.chat_simple.mq;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AsyncProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void asyncSend(String topic, String msg) {
        rocketMQTemplate.asyncSend(topic, MessageBuilder.withPayload(msg).build(), 
            new SendCallback() {
                @Override
                public void onSuccess(SendResult arg0) {
                    log.info("topic:{},消息生产端推送成功", topic);
                }

                @Override
                public void onException(Throwable err) {
                    log.info("topic:{},消息生产端推送异常", topic);
                    log.error("topic:" + topic + ",消息生产端推送异常", err);
                }
            }
        );
    }
    
}
