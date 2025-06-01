package com.hch.chat_simple.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.hch.chat_simple.pojo.dto.TokenInfoDTO;
import com.hch.chat_simple.pojo.dto.WebSocketPerssionVerify;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.HttpUrlUtils;
import com.hch.chat_simple.util.TokenUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
@Component
public class PermisionWsHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Value("${ws.netty.path:/chat}")
    private String wsPath;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String uri = request.uri();
            log.info("ws uri: {}", uri);
            HttpHeaders headers = request.headers();
            String hv = headers.get("X-User-Route");
            log.info("hv : {}", hv);
            if (StringUtils.isNotBlank(uri)) {
                MultiValueMap<String, String> map = HttpUrlUtils.getUriParams(uri);
                String token = map.getFirst("token");
                AttributeKey<WebSocketPerssionVerify> key = AttributeKey.valueOf(Constant.NETTY_CHANNEL_CTX_PERMISSION);
                WebSocketPerssionVerify verify = ctx.channel().attr(key).get();
                if (verify == null) {
                    verify = new WebSocketPerssionVerify();
                    verify.setToken(token);
                    TokenInfoDTO info = TokenUtil.parseTokenInfo(token);
                    if (info != null) {
                        verify.setUserId(info.getUserId());
                        verify.setUsername(info.getUsername());
                        verify.setRealName(info.getRealName());
                    } else {
                        // 校验不通过
                    }
                }

                ctx.channel().attr(key).setIfAbsent(verify);
                request.setUri(wsPath);
                ctx.fireChannelRead(request.retain());

            }
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ws exception:", cause);
        ctx.close();
    }

    @Override 
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        ctx.fireChannelRead(msg);
    }

    
}
