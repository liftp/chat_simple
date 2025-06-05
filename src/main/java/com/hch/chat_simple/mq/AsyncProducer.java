package com.hch.chat_simple.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AsyncProducer {

    @Autowired
    private DefaultMQProducer mqProducer;
    

    @Value("${rocketmq.consumer.group}")
    private String multiGroup;
    @Value("${rocketmq.consumer.single}")
    private String singleGroup;
    @Value("${rocketmq.consumer.composition}")
    private String compositionGroup;


    /**
     * 群聊消息发送
     * @param topic
     * @param tag
     * @param msg
     * @throws MQClientException
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void asyncSend(String topic, String tag, String msg) {
       
        Message msgBody = new Message(topic, tag, msg.getBytes());
        try {
            mqProducer.send(msgBody, new SendCallback() {
                @Override
                public void onSuccess(SendResult arg0) {
                    log.info("topic:{},消息生产端推送成功", topic);
                }

                @Override
                public void onException(Throwable err) {
                    log.info("topic:{},消息生产端推送异常", topic);
                    log.error("topic:" + topic + ",消息生产端推送异常", err);
                }
            });
        } catch (MQClientException | RemotingException | InterruptedException e) {
            log.info("多聊消息发送异常", e);
        }
    }

    
}
