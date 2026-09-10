package com.hch.chat_simple.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.handler.ChannelSendIfPresentHandler;
import com.hch.chat_simple.service.ICompositionConsumeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 好友上线通知消费：收到 composition 消息后，推送给目标用户的 ws channel
 */
@Slf4j
@Service
public class OnlineStatusConsumeImpl implements ICompositionConsumeService {

    @Autowired
    private ChannelSendIfPresentHandler handler;

    @Override
    public void consumeBusiness(Long chKey, String msg) {
        handler.handle(chKey, msg, () -> {});
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.UP_LINE;
    }
}
