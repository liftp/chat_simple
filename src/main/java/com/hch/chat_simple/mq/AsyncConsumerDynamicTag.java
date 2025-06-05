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
    
    @Value("${mq.topic.composition}")
    private String compositionTopic;

    @Value("${rocketmq.consumer.composition}")
    private String compositionGroup;

    @Value("${rocketmq.name-server}")
    private String namerServer;

    @Autowired
    private Environment environment;

    @Autowired
    private AsyncConsumerMuiltChat asyncConsumerMuiltChat;

    @Autowired
    private AsyncConsumerSingleChat asyncConsumerSingleChat;

    @Autowired
    private AsyncConsumerCompositionBusiness asyncConsumerCompositionBusiness;

    /**
     * 群聊消费
     * @return
     * @throws Throwable
     */
    @Bean
    public DefaultMQPushConsumer defaultMultiChatPushConsumer() throws Throwable {
        // 拉取本机的实例ip+port对应的映射实例数量，作为tag
        String mapValue = RedisUtil.mapGet(Constant.INST_WITH_MAP_KEY, getHost() + ":" + getPort());
        log.info("实例信息：{}, 映射tag: {}",  getHost() + ":" + getPort(), mapValue);
        // 相同消费组的 订阅关系 + 消费逻辑必须保持一致，所以只能根据不同组去消费不同的订阅关系，
        // 这里的消费关系是：(不同的组消费 --- 相同的topic + 不同的tag)
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(multiGroup + mapValue);
        consumer.subscribe(multiTopic, mapValue);
        log.info("multi topic: {}", multiTopic);
        consumer.setNamesrvAddr(namerServer);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                msgs.forEach(msg -> {
                    log.info("topic: {}, tag: {}, 消息消费了..", multiTopic, mapValue);
                    asyncConsumerMuiltChat.onMessage(new String(msg.getBody()));
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            
        });
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.start();
        log.info("multi consumer started...");
        return consumer;
    }


    /**
     * 单聊消费
     * @return
     * @throws Throwable
     */
    @Bean
    public DefaultMQPushConsumer defaultSingleChatPushConsumer() throws Throwable {
        // 拉取本机的实例ip+port对应的映射实例数量，作为tag
        String mapValue = RedisUtil.mapGet(Constant.INST_WITH_MAP_KEY, getHost() + ":" + getPort());
        log.info("实例信息：{}, 映射tag: {}",  getHost() + ":" + getPort(), mapValue);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(singleGroup + mapValue);
        consumer.setNamesrvAddr(namerServer);
        consumer.subscribe(singleTopic, mapValue.trim());
        log.info("single topic: {}", singleTopic);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("topic: {}, tag: {}, 消息消费了..", singleTopic, mapValue);
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

    @Bean
    public DefaultMQPushConsumer defaulCompositionNotifyPushConsumer() throws Throwable {
        // 拉取本机的实例ip+port对应的映射实例数量，作为tag
        String mapValue = RedisUtil.mapGet(Constant.INST_WITH_MAP_KEY, getHost() + ":" + getPort());
        log.info("实例信息：{}, 映射tag: {}",  getHost() + ":" + getPort(), mapValue);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(compositionGroup + mapValue);
        consumer.setNamesrvAddr(namerServer);
        consumer.subscribe(compositionTopic, mapValue.trim());
        log.info("复合消息通知 topic: {}", compositionTopic);
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("topic: {}, tag: {}, 消息消费了..", compositionTopic, mapValue);
                msgs.forEach(msg -> {
                    asyncConsumerCompositionBusiness.onMessage(new String(msg.getBody()));
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
