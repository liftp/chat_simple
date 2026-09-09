package com.hch.chat_simple.handler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.config.NettyGroup;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.mq.AsyncProducer;
import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.pojo.dto.TokenInfoDTO;
import com.hch.chat_simple.pojo.dto.WebSocketPerssionVerify;
import com.hch.chat_simple.pojo.po.ChatMsgPO;
import com.hch.chat_simple.service.IChatMsgService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.InstanceMapTagUtils;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.SnowflakeIdGen;
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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端向服务器发消息，已改为http发送，所以这里的channelRead0实际不会触发
 * 服务器向客户端发消息，还是netty接管，因为用netty管理了websocket会话，
 * 在userEventTriggered事件进行身份认证，及会话添加
 */
@Slf4j
@Component
@Sharable
public class WebSocketChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 暂时用Map管理channel，后续使用外部缓存处理
    static final Map<Long, ChannelId> channelMap = NettyGroup.getUserMapChannel();
    static final ChannelGroup channelGroup = NettyGroup.getChannelGroup();
    static final ExecutorService EXECUTOR_FIXED = Executors.newFixedThreadPool(16);

    @Value("${mq.topic.multi-chat}")
    private String multiChatTopic;

    @Value("${mq.topic.single-chat}")
    private String singleChatTopic;

    @Resource
    private IChatMsgService iChatMsgService;

    // @Resource
    // private SnowflakeIdGen snowflakeIdGen;

    @Autowired
    private AsyncProducer asyncProducerMuiltChat;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        log.info("server receive msg:{}", msg.text());
        // 粘包/拆包处理：超过 MSG_MAX_LENGTH 字符的消息记录告警，便于定位长消息/分片异常
        if (msg.text() != null && msg.text().length() > Constant.MSG_MAX_LENGTH) {
            log.warn("接收消息长度={} 超过阈值 {}，触发粘包/拆包告警，channelId={}",
                    msg.text().length(), Constant.MSG_MAX_LENGTH, ctx.channel().id().asLongText());
        }
        // msg是json结构，需要提取发送人
        ChatMsgDTO msgObj = JSON.parseObject(msg.text(), ChatMsgDTO.class);

        // 语音通话信令，只实时转发给对端channel，不落库
        if (MsgTypeEnum.VOICE_SIGNAL.getType().equals(msgObj.getMsgType())) {
            forwardSignal(ctx, msgObj);
            return;
        }
        
        // TODO 根据发送人查看是否在线
        // 在线，直接发送
        if (MsgTypeEnum.SEND_MSG.getType().equals(msgObj.getMsgType()) && msgObj.getReceiveUserId() != null) {
            WebSocketPerssionVerify  verify = getUserInfo(ctx);
            msgObj.setSendUserId(verify.getUserId());
            LocalDateTime now = LocalDateTime.now();
            msgObj.setCreatedAt(now);
            msgObj.setDateTime(now.atZone(ZoneId.of(Constant.ZONED_SHANGHAI)).toInstant().toEpochMilli() + "");
            // 先存储消息，然后拿到msgId
            ChatMsgPO chatMsg = BeanConvert.convertSingle(msgObj, ChatMsgPO.class);
            chatMsg.setCreatorId(verify.getUserId());
            chatMsg.setCreatorBy(verify.getUsername());
            chatMsg.setChatType(msgObj.getChatType());
            chatMsg.setStatus(Constant.MSG_SEND_FAILED); // 默认发送失败，成功后异步修改状态
            chatMsg.setMsgType(MsgTypeEnum.SEND_MSG.getType());
            chatMsg.setDr(Constant.NOT_DELETE);
            iChatMsgService.save(chatMsg);

            msgObj.setMsgId(chatMsg.getId());
            if (Constant.SINGLE_CHAT.equals(msgObj.getChatType())) {
                // 使用新版api, 发送消息不用ws，这里暂时注释
                // asyncProducerMuiltChat.asyncSendSingle(singleChatTopic, JSON.toJSONString(msgObj));
            } else if ((Constant.MUILT_CHAT.equals(msgObj.getChatType()))) {
                // 使用新版api, 发送消息不用ws，这里暂时注释
                // 群聊消息推送所有实例，进行广播
                // asyncProducerMuiltChat.asyncSendMulti(multiChatTopic, JSON.toJSONString(msgObj));
            }
            
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

                // 握手阶段 PermisionWsHandler 已用 Redis accessToken 校验并填充 userId（新版 token 机制）。
                // Redis UUID 不是 JWT，直接走旧版 parseTokenInfo 会解码失败（JWTDecodeException），
                // 导致 channel 不注册进 channelMap、用户收不到推送；仅当 verify 未带 userId（旧版 JWT token）时才兜底解析
                if (verify.getUserId() == null) {
                    TokenInfoDTO info =  TokenUtil.parseTokenInfo(token);
                    if (info == null) {
                        // token 失效处理
                        return;
                    }
                    verify.setUserId(info.getUserId());
                    ctx.channel().attr(key).setIfAbsent(verify);
                }

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
            } else {
                // TODO 发送未登录消息
                return;
            }

            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.ALL_IDLE) {
                    // Channel channel = ctx.channel();
                    removeChannelId(ctx);
                }
            }
        }
    }

    /**
     * 通话信令转发：content 是前端约定的信令json，服务端只覆写真实发送人后投递。
     * ws 按 userId 哈希分散在多个实例上，主叫所在实例的 channelMap 里没有对端 channel，
     * 所以和单聊消息一样按对端 userId 算 tag 投 MQ，由对端所在实例消费下发（见 AsyncConsumerSingleChat）
     */
    private void forwardSignal(ChannelHandlerContext ctx, ChatMsgDTO msgObj) {
        WebSocketPerssionVerify verify = getUserInfo(ctx);
        Long from = verify == null ? null : verify.getUserId();
        Long to = msgObj.getReceiveUserId();
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        // 不信任客户端上报的发送人
        msgObj.setSendUserId(from);
        asyncProducerMuiltChat.asyncSend(singleChatTopic,
            InstanceMapTagUtils.singleIdMapTag(to) + "", JSON.toJSONString(msgObj));
    }

    private void removeChannelId(ChannelHandlerContext ctx) {
        Long userId = getUserId(ctx);
        // 缓存移除
        if (userId != null) {
            ChannelId channelId = channelMap.remove(userId);
            Channel ch = channelGroup.find(channelId);
            if (ch != null) {
                channelGroup.remove(ch);
            }
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
