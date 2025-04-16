package com.hch.chat_simple.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.enums.YesOrNo;
import com.hch.chat_simple.handler.ChannelSendIfPresentHandler;
import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.service.ICompositionConsumeService;
import com.hch.chat_simple.service.IFriendRelationshipService;
import com.hch.chat_simple.util.MsgBodyResolveUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApplyFriendResultConsumeImpl implements ICompositionConsumeService {


    @Autowired
    private ChannelSendIfPresentHandler handler;

    
    @Override
    public void consumeBusiness(Long chKey, String msg) {
        List<String> content = MsgBodyResolveUtil.bodySplitByDelimiter(msg, 1);
        // msg 去掉msgType再转
        ApplyFriendDTO applyFriend = JSON.parseObject(content.get(1), ApplyFriendDTO.class);
        // 注意：这里可以屏蔽被申请人对申请人的备注信息，然后发送，暂时未处理
        // 通知申请方结果，保持前端约定格式 msgType + "," + msgObj,这里和后端的不同，不用加userId
        handler.handle(applyFriend.getProposerId(), msg, () -> {});
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.APPLY_FRIEND_RESULT;
    }
    
}
