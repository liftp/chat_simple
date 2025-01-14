package com.hch.chat_simple.service;

import com.hch.chat_simple.pojo.dto.ChatMsgDTO;

public interface IMsgSenderService {
    
    void sendMsg(ChatMsgDTO msg);

    void sendMsgMulti(ChatMsgDTO msgToGroup);
}
