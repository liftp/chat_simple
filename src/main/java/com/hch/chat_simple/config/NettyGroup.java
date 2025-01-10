package com.hch.chat_simple.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class NettyGroup {
    // channel group,存储用户连接
    private static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // private static final 

    public static ChannelGroup getChannelGroup() {
        return CHANNEL_GROUP;
    }

    // 存储用户和channel 映射
    private static final Map<Long, ChannelId> USER_MAP_CHANNEL = new ConcurrentHashMap<>();

    public static Map<Long, ChannelId> getUserMapChannel() {
        return USER_MAP_CHANNEL;
    }


}
