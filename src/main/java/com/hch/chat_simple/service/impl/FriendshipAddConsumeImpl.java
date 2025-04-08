package com.hch.chat_simple.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.handler.ChannelSendIfPresentHandler;
import com.hch.chat_simple.pojo.vo.FriendRelationshipVO;
import com.hch.chat_simple.service.ICompositionConsumeService;
import com.hch.chat_simple.util.MsgBodyResolveUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FriendshipAddConsumeImpl implements ICompositionConsumeService {


    @Autowired
    private ChannelSendIfPresentHandler handler;
    
    @Override
    public void consumeBusiness(Long chKey, String msg) {
        List<String> content = MsgBodyResolveUtil.bodySplitByDelimiter(msg, 1);
        FriendRelationshipVO applyFriend = JSON.parseObject(content.get(1), FriendRelationshipVO.class);
        // 注意：这里可以屏蔽被申请人对申请人的备注信息，然后发送，暂时未处理
        // 通知申请方结果，保持前端约定格式 msgType + "," + msgObj,这里和后端的不同，不用加userId
        handler.handle(applyFriend.getCreatorId(), msg, () -> {});
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.FRIEND_SHIP_ADD;
    }
    
}
