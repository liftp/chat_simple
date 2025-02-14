package com.hch.chat_simple.service;

import com.hch.chat_simple.enums.MsgTypeEnum;

public interface ICompositionConsumeService {
    
    void consumeBusiness(Long chKey, String msg);

    MsgTypeEnum getMsgType();
}
