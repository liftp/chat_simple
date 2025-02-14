package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.mapper.ApplyFriendMapper;
import com.hch.chat_simple.mq.AsyncProducer;
import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.po.ApplyFriendPO;
import com.hch.chat_simple.service.IApplyFriendService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 好友申请记录 服务实现类
 * </p>
 *
 * @author hch
 * @since 2025-02-14
 */
@Slf4j
@Service
public class ApplyFriendServiceImpl extends ServiceImpl<ApplyFriendMapper, ApplyFriendPO> implements IApplyFriendService {

    @Autowired
    private AsyncProducer asyncProducer;

    @Value("${mq.topic.composition}")
    private String compositionTopicName;
    
    @Override
    public void applyFriend(ApplyFriendDTO applyFriend) {
        Long userId = ContextUtil.getUserId();
        String userName = ContextUtil.getUsername();
        ApplyFriendPO po = BeanConvert.convertSingle(applyFriend, ApplyFriendPO.class);
        po.setProposerId(userId);
        po.setProposerName(userName);
        po.setCreatedAt(LocalDateTime.now());
        po.setCreatorBy(userName);
        po.setCreatorId(userId);
        
        save(po);

        // 推送申请消息, 约定：消息类型+','+消息体，这样后续直接解析类型，之后再转对应的消息内容
        asyncProducer.asyncSend(compositionTopicName, MsgTypeEnum.APPLY_FRIEND.getType() + "," + JSON.toJSONString(po));
    }

}
