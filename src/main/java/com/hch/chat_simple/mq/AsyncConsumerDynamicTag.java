package com.hch.chat_simple.mq;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.hch.chat_simple.config.InstanceCreateHook;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 这里的消费者订阅在实例信息注入后处理，这样能获取到实例信息，作为tag进行路由
 */
@Slf4j
@Configuration
@AutoConfigureAfter(InstanceCreateHook.class)
public class AsyncConsumerDynamicTag {

    @Value("${mq.topic.multi-chat}")
    private String multiTopic;

    @Value("${rocketmq.consumer.group}")
    private String multiGroup;

    @Value("${mq.topic.single-chat}")
    private String singleTopic;

    @Value("${rocketmq.consumer.single}")
    private String singleGroup;

    @Value("${rocketmq.name-server}")
    private String namerServer;

    @Autowired
    private Environment environment;

    @Autowired
    private AsyncConsumerMuiltChat asyncConsumerMuiltChat;

    @Autowired
    private AsyncConsumerSingleChat asyncConsumerSingleChat;

    /**
     * 群聊消费
     * @return
     * @throws Throwable
     */
    @Bean
    public DefaultMQPushConsumer defaultMultiChatPushConsumer() throws Throwable {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(multiGroup);
        // 拉取本机的实例ip+port对应的映射实例数量，作为tag
        String mapValue = RedisUtil.mapGet(Constant.INST_WITH_MAP_KEY, getHost() + ":" + getPort());
        log.info("实例信息：{}, 映射tag: {}",  getHost() + ":" + getPort(), mapValue);
        consumer.subscribe(multiTopic, mapValue);
        log.info("multi topic: {}", multiTopic);
        consumer.setNamesrvAddr(namerServer);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                msgs.forEach(msg -> {
                    asyncConsumerMuiltChat.onMessage(new String(msg.getBody()));
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            
        });
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.start();
        return consumer;
    }


    /**
     * 单聊消费
     * @return
     * @throws Throwable
     */
    @Bean
    public DefaultMQPushConsumer defaultSingleChatPushConsumer() throws Throwable {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(singleGroup);
        // 拉取本机的实例ip+port对应的映射实例数量，作为tag
        String mapValue = RedisUtil.mapGet(Constant.INST_WITH_MAP_KEY, getHost() + ":" + getPort());
        log.info("实例信息：{}, 映射tag: {}",  getHost() + ":" + getPort(), mapValue);
        consumer.setNamesrvAddr(namerServer);
        consumer.subscribe(singleTopic, mapValue);
        log.info("single topic: {}", singleTopic);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("消息消费了..");
                msgs.forEach(msg -> {
                    asyncConsumerSingleChat.onMessage(new String(msg.getBody()));
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            
        });
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.start();
        return consumer;
    }


    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPort() {
        return environment.getProperty("server.port");
    }
}
