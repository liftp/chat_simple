package com.hch.chat_simple.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.config.NettyGroup;
import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.service.IMsgSenderService;
import com.hch.chat_simple.util.BeanConvert;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MsgSenderServiceImpl implements IMsgSenderService {
  
    private final Map<Long, ChannelId> userMapChannel = NettyGroup.getUserMapChannel();
    private final ChannelGroup channelGroup = NettyGroup.getChannelGroup();
    

    /**
     * 单发消息
     */
    @Override
    public void sendMsg(ChatMsgDTO msg) {
        ChannelId userChannelId = userMapChannel.get(msg.getReceiveUserId());
        if (userChannelId != null) {
            Channel channel = channelGroup.find(userChannelId);
            if (channel != null) {
                ChannelFuture future = channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
                future.addListener((result) -> {
                    // 接收成功或失败，记录状态，失败下次登录拉取
                    boolean receiveStatus = result.isSuccess();
                    log.info("接收状态：{}", receiveStatus);
                    // record msg
                });
                return;
            }
        }
        // 未找到用户，msg record
        
    }

    /**
     * 群发
     */
    @Override
    public void sendMsgMulti(ChatMsgDTO msgToGroup) {
        // 查询群聊用户id
        List<Long> groupUsers = new ArrayList<>();
        // 校验下是否包含当前用户，否则不在该群聊，没权限发送
        boolean inGroup = groupUsers.contains(msgToGroup.getSendUserId());
        if (inGroup) {
            // 过滤当前用户
            groupUsers.stream().filter(e -> msgToGroup.getSendUserId() != e).forEach(user -> {
                ChatMsgDTO msgToUser = BeanConvert.convertSingle(msgToGroup, ChatMsgDTO.class);
                msgToUser.setReceiveUserId(user);
                sendMsg(msgToUser);
            });
        } else {
            // msg to sender not permission
        }

    }
}
