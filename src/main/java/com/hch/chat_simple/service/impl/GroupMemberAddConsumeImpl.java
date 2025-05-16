package com.hch.chat_simple.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.handler.ChannelSendIfPresentHandler;
import com.hch.chat_simple.service.ICompositionConsumeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 成员添加消息消费实现类
 */
@Slf4j
@Service
public class GroupMemberAddConsumeImpl implements ICompositionConsumeService {

    @Autowired
    private ChannelSendIfPresentHandler handler;

    @Override
    public void consumeBusiness(Long chKey, String msg) {
        log.info("添加群聊成员消费：{}", msg);
        handler.handle(chKey, msg, () -> {});
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.GROUP_MEMBER_ADD;
    }
    
}
