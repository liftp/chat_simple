package com.hch.chat_simple.mq;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.config.NettyGroup;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.pojo.po.ChatMsgPO;
import com.hch.chat_simple.service.IChatMsgService;
import com.hch.chat_simple.util.Constant;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;


@Component
@RocketMQMessageListener(
    topic = "${mq.topic.single-chat}",
    consumerGroup = "${rocketmq.consumer.single}",
    consumeMode = ConsumeMode.CONCURRENTLY,
    messageModel = MessageModel.BROADCASTING
)
public class AsyncConsumerSingleChat  implements RocketMQListener<String> {
    
    // 暂时用Map管理channel，后续使用外部缓存处理
    static final Map<Long, ChannelId> channelMap = NettyGroup.getUserMapChannel();
    static final ChannelGroup channelGroup = NettyGroup.getChannelGroup();
    static final ExecutorService EXECUTOR_FIXED = Executors.newFixedThreadPool(16);

    @Autowired
    private IChatMsgService iChatMsgService;

    
    public void singleChatMsgConsume(String msg) {
        ChatMsgDTO msgObj = JSON.parseObject(msg, ChatMsgDTO.class);
        // 在线，直接发送
        if (MsgTypeEnum.SEND_MSG.getType().equals(msgObj.getMsgType()) && msgObj.getReceiveUserId() != null) {
            // TODO 发送用户是否有权限，进行校验
            if (Constant.SINGLE_CHAT.equals(msgObj.getChatType())) {
                msgObj.setFriendId(msgObj.getSendUserId());
                // 在线直接发送
                channelMap.computeIfPresent(msgObj.getReceiveUserId(), (k, v) -> {
                    Channel channel = channelGroup.find(v);
                    if (channel != null) {
                        // 约定格式 msgType + "," + msgObj
                        ChannelFuture sendFuture = channel.writeAndFlush(new TextWebSocketFrame(MsgTypeEnum.SEND_MSG.getType() + "," + JSON.toJSONString(msgObj)));
                        sendFuture.addListener(future -> {
                            if (future.isSuccess()) {
                                ChatMsgPO updateMsgStatus = new ChatMsgPO();
                                updateMsgStatus.setId(msgObj.getMsgId());
                                updateMsgStatus.setStatus(Constant.MSG_SEND_SUCCESSED);
                                iChatMsgService.updateById(updateMsgStatus);
                            }
                        });
                    }
                    return v;
                });
            }
        }
    }


    @Override
    public void onMessage(String message) {
        try {
            singleChatMsgConsume(message);
        } catch (Exception e) {
            throw new RuntimeException("单聊消息消费失败", e);
        }
    }
}
