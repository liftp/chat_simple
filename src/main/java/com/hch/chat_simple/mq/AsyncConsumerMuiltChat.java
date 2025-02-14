package com.hch.chat_simple.mq;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
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
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hch.chat_simple.config.NettyGroup;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.pojo.po.GroupMemberPO;
import com.hch.chat_simple.service.IGroupMemberService;
import com.hch.chat_simple.util.Constant;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@Component
@RocketMQMessageListener(
    topic = "${mq.topic.multi-chat}",
    consumerGroup = "${rocketmq.consumer.group}",
    consumeMode = ConsumeMode.CONCURRENTLY,
    messageModel = MessageModel.BROADCASTING
)
public class AsyncConsumerMuiltChat implements RocketMQListener<String> {
    // 暂时用Map管理channel，后续使用外部缓存处理
    static final Map<Long, ChannelId> channelMap = NettyGroup.getUserMapChannel();
    static final ChannelGroup channelGroup = NettyGroup.getChannelGroup();
    static final ExecutorService EXECUTOR_FIXED = Executors.newFixedThreadPool(16);

    // @Autowired
    // private IChatMsgService iChatMsgService;
    @Autowired
    private IGroupMemberService iGroupMemberService;

    @Override
    public void onMessage(String message) {
        try {
            muiltChatMsgConsume(message);
        } catch (Exception e) {
            throw new RuntimeException("消息消费失败", e);
        }
    }

    public void muiltChatMsgConsume(String msg) {
        ChatMsgDTO msgObj = JSON.parseObject(msg, ChatMsgDTO.class);
        // 在线，直接发送
        if (MsgTypeEnum.SEND_MSG.getType().equals(msgObj.getMsgType()) && msgObj.getGroupId() != null) {
            // 查询群聊的用户，遍历（除了发送人）进行发送，后续可以缓存优化
            List<GroupMemberPO> members = selectGroupMemberById(msgObj.getGroupId());
            // TODO 发送用户是否有权限，进行校验
            if (Constant.MUILT_CHAT.equals(msgObj.getChatType())) {
                members.forEach(receiveUser -> {
                    if (!receiveUser.getId().equals(msgObj.getSendUserId())) {
                        // msgObj.setReceiveUserId(receiveUser.getId());
                        msgObj.setFriendId(Constant.SINGLE_CHAT.equals(msgObj.getChatType()) ? msgObj.getSendUserId() : msgObj.getGroupId());
                        // 在线直接发送
                        channelMap.computeIfPresent(receiveUser.getId(), (k, v) -> {
                            Channel channel = channelGroup.find(v);
                            if (channel != null) {
                                channel.writeAndFlush(new TextWebSocketFrame(msg));
                            }
                            return v;
                        });
                    }
                });
            }
        }
    }

    public List<GroupMemberPO> selectGroupMemberById(Long id) {
        Wrapper<GroupMemberPO> query = Wrappers.<GroupMemberPO>query().lambda()
            .eq(GroupMemberPO::getGroupId, id);
        return iGroupMemberService.list(query);
    }
    
}
