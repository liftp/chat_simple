package com.hch.chat_simple.handler;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.hch.chat_simple.config.NettyGroup;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@Component
public class ChannelSendIfPresentHandler {
    
    // 暂时用Map管理channel，后续使用外部缓存处理
    static final Map<Long, ChannelId> channelMap = NettyGroup.getUserMapChannel();
    static final ChannelGroup channelGroup = NettyGroup.getChannelGroup();
        
    public void handle(Long channelKey, String msg, Runnable execute) {
        channelMap.computeIfPresent(channelKey, 
            (k, v) -> {
                Channel ch = channelGroup.find(v);
                if (ch != null) {
                    execute.run();
                    ch.writeAndFlush(new TextWebSocketFrame(msg));
                }
                return v;
            }
        );
    }
}
