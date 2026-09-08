package com.hch.chat_simple.mq;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hch.chat_simple.config.NettyGroup;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.pojo.po.ChatMsgPO;
import com.hch.chat_simple.service.IChatMsgService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.InstanceMapTagUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
// @RocketMQMessageListener(
//     topic = "${mq.topic.single-chat}",
//     consumerGroup = "${rocketmq.consumer.single}",
//     consumeMode = ConsumeMode.CONCURRENTLY,
//     messageModel = MessageModel.BROADCASTING
// )
public class AsyncConsumerSingleChat {
    
    // 暂时用Map管理channel，后续使用外部缓存处理
    static final Map<Long, ChannelId> channelMap = NettyGroup.getUserMapChannel();
    static final ChannelGroup channelGroup = NettyGroup.getChannelGroup();
    static final ExecutorService EXECUTOR_FIXED = Executors.newFixedThreadPool(16);

    @Autowired
    private IChatMsgService iChatMsgService;

    @Autowired
    private AsyncProducer asyncProducer;

    @Value("${mq.topic.single-chat}")
    private String singleChatTopic;

    
    public void singleChatMsgConsume(String msg) {
        ChatMsgDTO msgObj = JSON.parseObject(msg, ChatMsgDTO.class);
        // 通话信令：ws 按 userId 哈希分散在多实例，这里已在对端所在实例上
        if (MsgTypeEnum.VOICE_SIGNAL.getType().equals(msgObj.getMsgType())) {
            pushSignal(msgObj);
            return;
        }
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


    /**
     * 通话信令下发：本实例有对端 channel 就直发；没有且是来电邀请(offer)才回 offline 给发起方，
     * 回执同样按发起方 userId 算 tag 投 MQ。ice/answer/bye 这类过期信令找不到人直接丢，
     * 也避免双方都不在线时 offline 互相回执在 MQ 里打环。
     */
    private void pushSignal(ChatMsgDTO msgObj) {
        Long to = msgObj.getReceiveUserId();
        ChannelId targetId = to == null ? null : channelMap.get(to);
        Channel target = targetId == null ? null : channelGroup.find(targetId);
        if (target != null) {
            target.writeAndFlush(new TextWebSocketFrame(MsgTypeEnum.VOICE_SIGNAL.getType() + "," + JSON.toJSONString(msgObj)));
            return;
        }

        String type = null;
        String callId = null;
        try {
            JSONObject content = JSON.parseObject(msgObj.getContent());
            type = content.getString("type");
            callId = content.getString("callId");
        } catch (Exception e) {
            log.warn("信令content格式异常: {}", msgObj.getContent());
        }
        if (!"offer".equals(type)) {
            return;
        }

        JSONObject offline = new JSONObject();
        offline.put("callId", callId);
        offline.put("type", "offline");
        ChatMsgDTO back = BeanConvert.convertSingle(msgObj, ChatMsgDTO.class);
        back.setSendUserId(to);
        back.setReceiveUserId(msgObj.getSendUserId());
        back.setContent(offline.toJSONString());
        Long from = back.getReceiveUserId();
        if (from == null) {
            return;
        }
        asyncProducer.asyncSend(singleChatTopic, InstanceMapTagUtils.singleIdMapTag(from) + "", JSON.toJSONString(back));
    }


    public void onMessage(String message) {
        try {
            singleChatMsgConsume(message);
        } catch (Exception e) {
            throw new RuntimeException("单聊消息消费失败", e);
        }
    }
}
