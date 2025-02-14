package com.hch.chat_simple.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.handler.ChannelSendIfPresentHandler;
import com.hch.chat_simple.service.ICompositionConsumeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApplyFriendConsumeImpl implements ICompositionConsumeService {


    @Autowired
    private ChannelSendIfPresentHandler handler;
    
    @Override
    public void consumeBusiness(Long chKey, String msg) {
        handler.handle(chKey, msg, () -> {});
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.APPLY_FRIEND;
    }
    
}
