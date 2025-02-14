package com.hch.chat_simple.mq;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hch.chat_simple.service.ICompositionConsumeService;


@Component
@RocketMQMessageListener(
    topic = "${mq.topic.composition}",
    consumerGroup = "${rocketmq.consumer.composition}",
    consumeMode = ConsumeMode.CONCURRENTLY,
    messageModel = MessageModel.BROADCASTING
)
public class AsyncConsumerCompositionBusiness implements RocketMQListener<String> {


    @Autowired
    private List<ICompositionConsumeService> compositionService;

    @Override
    public void onMessage(String message) {
        // 约定：消息类型+','+用户id(channel绑定的key)+消息体，这样后续直接解析类型，之后再转对应的消息内容
        int index = message.indexOf(",");
        Function<String, Boolean> checkMsgAndNum = str -> StringUtils.isNotBlank(str) && str.matches("\\d+");
        if (index != -1) {
            int msgType = 0;
            String typeStr = message.substring(0, index);
            if (checkMsgAndNum.apply(typeStr)) {
                msgType = Integer.parseInt(typeStr);
                int secondIndex = typeStr.indexOf(",");
                String secondStr = secondIndex < index + 1 ? "" : typeStr.substring(index + 1, secondIndex);
                if (checkMsgAndNum.apply(secondStr)) {
                    msgConsume(msgType, Long.parseLong(secondStr), typeStr.substring(index + 1));
                }
            }
            
        }
    }

   private void msgConsume(int msgType, Long chKey, String msg) {
        compositionService.stream()
            .filter(e -> e.getMsgType().getType() == msgType)
            .forEach(e -> e.consumeBusiness(chKey, msg));
    }
    
}
