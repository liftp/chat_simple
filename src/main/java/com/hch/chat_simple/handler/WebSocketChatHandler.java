package com.hch.chat_simple.handler;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.config.NettyGroup;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.pojo.dto.TokenInfoDTO;
import com.hch.chat_simple.pojo.dto.WebSocketPerssionVerify;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.TokenUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Sharable
public class WebSocketChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 暂时用Map管理channel，后续使用外部缓存处理
    static final Map<Long, ChannelId> channelMap = NettyGroup.getUserMapChannel();
    static final ChannelGroup channelGroup = NettyGroup.getChannelGroup();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        log.info("server receive msg:{}", msg.text());
        // msg是json结构，需要提取发送人
        ChatMsgDTO msgObj = JSON.parseObject(msg.text(), ChatMsgDTO.class);
        msgObj.setCreatedAt(LocalDateTime.now());
        // TODO 根据发送人查看是否在线
        // 在线，直接发送
        if (MsgTypeEnum.SEND_MSG.getType().equals(msgObj.getMsgType()) && msgObj.getReceiveUserId() != null) {
            channelMap.computeIfPresent(msgObj.getReceiveUserId(), (k, v) -> {
                channelGroup.find(v).writeAndFlush(new TextWebSocketFrame(msg.text()));
                return v;
            });
        }
        // TODO 离线，存储数据库，接收人上线拉取
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常：{}", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            AttributeKey<WebSocketPerssionVerify> key = AttributeKey.valueOf(Constant.NETTY_CHANNEL_CTX_PERMISSION);
            WebSocketPerssionVerify verify = ctx.channel().attr(key).get();
            if (verify != null) {
                String token = verify.getToken();
                if (StringUtils.isBlank(token)) {
                    // todo 用户未登录消息发送
                    return;
                }

                TokenInfoDTO info =  TokenUtil.parseTokenInfo(token);
                if (info == null) {
                    // token 失效处理
                    return;
                }
                verify.setUserId(info.getUserId());
                ctx.channel().attr(key).setIfAbsent(verify);

                Channel channel = ctx.channel();
                // 添加到channelMap中，便于发送消息
                if (verify != null) {

                    Long userId = verify.getUserId();
                    String username = verify.getUsername();
                    // TODO 上线检测拉取历史消息
                    log.info("用户{}, channel id:{}, 加入", username, channel.id().asLongText());
                    // 加入channel
                    channelMap.put(userId, channel.id());
                    channelGroup.add(channel);
                }
                // TODO 登录后，推送未读消息
            } else {
                // TODO 发送未登录消息
                return;
            }

            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.ALL_IDLE) {
                    Channel channel = ctx.channel();
                    removeChannelId(ctx);
                }
            }
        }
    }

    private void removeChannelId(ChannelHandlerContext ctx) {
        Long userId = getUserId(ctx);
        // 缓存移除
        if (userId != null) {
            channelMap.remove(userId);
        }
    }

    private Long getUserId(ChannelHandlerContext ctx) {
        AttributeKey<WebSocketPerssionVerify> key = AttributeKey.valueOf(Constant.NETTY_CHANNEL_CTX_PERMISSION);
        WebSocketPerssionVerify verify = ctx.channel().attr(key).get();
        if (verify != null) {
            return verify.getUserId();
        }
        return null;
    }

    private WebSocketPerssionVerify getUserInfo(ChannelHandlerContext ctx) {
        AttributeKey<WebSocketPerssionVerify> key = AttributeKey.valueOf(Constant.NETTY_CHANNEL_CTX_PERMISSION);
        WebSocketPerssionVerify verify = ctx.channel().attr(key).get();
        if (verify != null) {
            return verify;
        }
        return null;
    }
    
}
