package com.hch.chat_simple.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * 群聊天处理器
 */
public class WebSocketMultipleFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    static final Map<String, Channel> channelMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        
        System.out.println("服务器端收到消息：" + msg.text());

        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalDateTime.now() + msg.text()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // web客户端连接后触发
        Channel channel = ctx.channel();
        System.out.println("客户端" + channel.id().asLongText() + "加入");
        
        // channelMap.put(null, channel);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("客户端" + channel.id().asLongText() + "离开");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("异常发生：" + cause.getMessage());
        ctx.close();
    }
    
}
