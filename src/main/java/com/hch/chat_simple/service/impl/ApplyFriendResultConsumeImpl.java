package com.hch.chat_simple.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.enums.YesOrNo;
import com.hch.chat_simple.handler.ChannelSendIfPresentHandler;
import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.service.ICompositionConsumeService;
import com.hch.chat_simple.service.IFriendRelationshipService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApplyFriendResultConsumeImpl implements ICompositionConsumeService {


    @Autowired
    private ChannelSendIfPresentHandler handler;
    @Autowired
    private IFriendRelationshipService iFriendRelationshipService;
    
    @Override
    public void consumeBusiness(Long chKey, String msg) {
        ApplyFriendDTO applyFriend = JSON.parseObject(msg, ApplyFriendDTO.class);
        // 通过，直接添加好友关系
        if (YesOrNo.YES.getCode().equals(applyFriend.getApplyPass())) {
            iFriendRelationshipService.insertFriendRelationship(applyFriend);
        }
        // 注意：这里可以屏蔽被申请人对申请人的备注信息，然后发送，暂时未处理
        // 通知申请方结果，保持前端约定格式 msgType + "," + msgObj,这里和后端的不同，不用加userId
        handler.handle(applyFriend.getProposerId(), MsgTypeEnum.APPLY_FRIEND_RESULT.getType() + "," + msg, () -> {});
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.APPLY_FRIEND_RESULT;
    }
    
}
