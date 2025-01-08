package com.hch.chat_simple.handler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.pojo.dto.SingleChatMsgDTO;
import com.hch.chat_simple.util.ContextUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketSingleChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    static final Map<Long, Channel> channelMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        log.info("server receive msg:{}", msg.text());
        // msg是json结构，需要提取发送人
        SingleChatMsgDTO msgObj = JSON.parseObject(msg.text(), SingleChatMsgDTO.class);
        msgObj.setServerDate(LocalDateTime.now());
        // TODO 根据发送人查看是否在线
        // 在线，直接发送
        channelMap.computeIfPresent(msgObj.getTo(), (k, v) -> {
            v.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msgObj)));
            return v;
        });
        // TODO 离线，存储数据库，接收人上线拉取
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Long userId = ContextUtil.getUserId();
        String username = ContextUtil.getUsername();
        // TODO 上线检测拉取历史消息
        log.info("用户{}, channel id:{}, 加入", username, channel.id().asLongText());
        // 加入channel
        channelMap.put(userId, channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("客户: {} 离开", ContextUtil.getUsername());
        channelMap.remove(ContextUtil.getUserId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常：{}", cause);
        ctx.close();
    }
    
}
