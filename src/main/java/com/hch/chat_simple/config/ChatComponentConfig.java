package com.hch.chat_simple.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.hch.chat_simple.handler.PermisionWsHandler;
import com.hch.chat_simple.handler.WebSocketChatHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Configuration
public class ChatComponentConfig {

    @Value("${chat.server.port}")
    private int port;

    private final String WS_PROTOCOL = "WebSocket";

    @Autowired
    private PermisionWsHandler permisionWsHandler;
    @Autowired
    private WebSocketChatHandler webSocketSingleChatHandler;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;


    @PostConstruct
    public ServerBootstrap serverBootstrap() {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(childHandler());
        CompletableFuture.runAsync(() -> {
            try {
                ChannelFuture channelFuture = boot.bind(port).sync();
                channelFuture.channel().closeFuture().sync();
                
            } catch (Exception e) {
                log.error("server boot strap 异常: ", e);
            }
        });
        return boot;
        
        
    }

    // @Bean
    public ChannelInitializer<SocketChannel> childHandler() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new ChunkedWriteHandler());
                pipeline.addLast(new HttpObjectAggregator(8192));
                pipeline.addLast(permisionWsHandler);

                pipeline.addLast(new WebSocketServerProtocolHandler("/chat", WS_PROTOCOL, true, 65536 * 10));
                // 业务handler
                pipeline.addLast(webSocketSingleChatHandler);
            }
        };
    }

    @PreDestroy
    private void destroy() throws InterruptedException {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().sync();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().sync();
        }
    }


    
}
