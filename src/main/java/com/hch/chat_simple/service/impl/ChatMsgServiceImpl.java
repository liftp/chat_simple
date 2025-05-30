package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.pojo.po.ChatMsgPO;
import com.hch.chat_simple.pojo.query.GroupNotReadMsgQuery;
import com.hch.chat_simple.pojo.query.GroupNotReadMsgQuery.SingleGroupParam;
import com.hch.chat_simple.pojo.vo.ChatMsgVO;
import com.hch.chat_simple.pojo.vo.GroupMemberVO;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.mapper.ChatMsgMapper;
import com.hch.chat_simple.mq.AsyncProducer;
import com.hch.chat_simple.service.IChatMsgService;
import com.hch.chat_simple.service.IGroupInfoService;
import com.hch.chat_simple.service.IGroupMemberService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.ContextUtil;
import com.hch.chat_simple.util.InstanceMapTagUtils;
import com.hch.chat_simple.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 聊天记录表 服务实现类
 * </p>
 *
 * @author hch
 * @since 2025-02-01
 */
@Service
@Slf4j
public class ChatMsgServiceImpl extends ServiceImpl<ChatMsgMapper, ChatMsgPO> implements IChatMsgService {

    
    @Value("${mq.topic.multi-chat}")
    private String multiChatTopic;

    @Value("${mq.topic.single-chat}")
    private String singleChatTopic;

    @Autowired
    private AsyncProducer asyncProducerMuiltChat;

    @Autowired
    private IGroupInfoService iGroupInfoService;

    @Override
    public List<ChatMsgVO> selectNotReadMsgMsg() {
        // 拉取单聊消息，接收人是自己, 未接收成功的所有数据
        Wrapper<ChatMsgPO> query = Wrappers.<ChatMsgPO>query().lambda()
            .eq(ChatMsgPO::getReceiveUserId, ContextUtil.getUserId())
            .eq(ChatMsgPO::getChatType, Constant.SINGLE_CHAT)
            .eq(ChatMsgPO::getStatus, Constant.MSG_SEND_FAILED);
        List<ChatMsgPO> notReadMsg = list(query);
        // 拉取后修改状态，不会重复拉取
        List<ChatMsgPO> updateList = notReadMsg.stream().map(e -> {
            ChatMsgPO updatePO = new ChatMsgPO();
            updatePO.setId(e.getId());
            updatePO.setStatus(Constant.MSG_SEND_SUCCESSED);
            return updatePO;
        }).collect(Collectors.toList());
        updateBatchById(updateList);
        return BeanConvert.convertList(notReadMsg, ChatMsgVO.class, 
            (src, trg) -> {
                trg.setMsgId(src.getId());
                if (src.getCreatedAt() != null) {
                    Long dateTime = src.getCreatedAt().atZone(ZoneId.of(Constant.ZONED_SHANGHAI)).toInstant().toEpochMilli();
                    // log.info("dateTime:{}", dateTime);
                    trg.setDateTime(dateTime);
                }
            });
    }

    @Override
    public ChatMsgVO sendMsg(ChatMsgDTO msgObj) {
        if (MsgTypeEnum.SEND_MSG.getType().equals(msgObj.getMsgType()) && msgObj.getReceiveUserId() != null) {
            Long userId = ContextUtil.getUserId();
            String username = ContextUtil.getUsername();
            msgObj.setSendUserId(userId);
            LocalDateTime now = LocalDateTime.now();
            msgObj.setCreatedAt(now);
            msgObj.setDateTime(now.atZone(ZoneId.of(Constant.ZONED_SHANGHAI)).toInstant().toEpochMilli() + "");
            // 先存储消息，然后拿到msgId
            ChatMsgPO chatMsg = BeanConvert.convertSingle(msgObj, ChatMsgPO.class);
            chatMsg.setCreatorId(userId);
            chatMsg.setCreatorBy(username);
            chatMsg.setChatType(msgObj.getChatType());
            chatMsg.setStatus(Constant.MSG_SEND_FAILED); // 默认发送失败，成功后异步修改状态
            chatMsg.setMsgType(MsgTypeEnum.SEND_MSG.getType());
            chatMsg.setDr(Constant.NOT_DELETE);
            save(chatMsg);

            msgObj.setMsgId(chatMsg.getId());

            
            if (Constant.SINGLE_CHAT.equals(msgObj.getChatType())) {
                // 根据接收人，分成不同的tag，进行单独发送
                int tag = InstanceMapTagUtils.singleIdMapTag(chatMsg.getReceiveUserId());
                asyncProducerMuiltChat.asyncSend(singleChatTopic + ":" + tag, JSON.toJSONString(msgObj));
            } else if ((Constant.MUILT_CHAT.equals(msgObj.getChatType()))) {
                // 查询所有用户，根据实例分成不同的tag，分别发送
                List<GroupMemberVO> groupMembers = iGroupInfoService.findGroupMemberById(chatMsg.getGroupId());
                List<Long> memberIds = groupMembers.stream().map(GroupMemberVO::getMemberId).collect(Collectors.toList());
                // 群聊消息推送所有实例，进行广播, 这里可以直接将要发送的人放入消息中，消费端拿到不去查，直接发送
                Map<Integer, List<Long>> groupTagMap = InstanceMapTagUtils.multiGroupByTag(memberIds);
                groupTagMap.entrySet().forEach(entry -> {
                    msgObj.setGroupToUserIds(entry.getValue());
                    asyncProducerMuiltChat.asyncSend(multiChatTopic + ":" + entry.getKey(), JSON.toJSONString(msgObj));
                });
            }
            
            return BeanConvert.convert(chatMsg, ChatMsgVO.class, 
                (src, trg) -> {
                    trg.setMsgId(src.getId());
                    trg.setDateTime(src.getCreatedAt().atZone(ZoneId.of(Constant.ZONED_SHANGHAI)).toInstant().toEpochMilli());
                });
        }
        return null;
    }

    @Override
    public List<ChatMsgVO> selectGroupChatMsgNotRead(GroupNotReadMsgQuery query) {
        if (CollectionUtils.isEmpty(query.getGroupList())) {
            return new ArrayList<>();
        }
        // 多层or可能会慢，也可以调整方案为：默认查前10条，10条后的内容每次前端点击群聊的时候再去查...
        LambdaQueryWrapper<ChatMsgPO> queryNotRead = Wrappers.<ChatMsgPO>lambdaQuery()
            .eq(ChatMsgPO::getChatType, Constant.MUILT_CHAT);
        // sql: 
        // select * from chat_msg 
        // where chat_type = 2
        //  and dr = 0
        //  and (
        //      (group_id = #{groupParam.groupId}
        //      and id > #{groupParam.msgId})
        //      or (group_id = #{groupParam.groupId}
        //      and id > #{groupParam.msgId})
        //      or ...
        //  )
        queryNotRead
            .and(qa -> {
                for (SingleGroupParam groupParam : query.getGroupList()) {
                    qa.or(subq -> {
                            subq.eq(ChatMsgPO::getGroupId, groupParam.getGroupId())
                                .gt(ChatMsgPO::getId, groupParam.getMsgId());
                    });
                }
            });
        List<ChatMsgPO> data = list(queryNotRead);

        return BeanConvert.convertList(data, ChatMsgVO.class, (src, trg) -> {
            trg.setMsgId(src.getId());
            trg.setDateTime(src.getCreatedAt().atZone(ZoneId.of(Constant.ZONED_SHANGHAI)).toInstant().toEpochMilli());
        });

    }

}
