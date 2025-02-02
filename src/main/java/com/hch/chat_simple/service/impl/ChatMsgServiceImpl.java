package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.pojo.po.ChatMsgPO;
import com.hch.chat_simple.pojo.vo.ChatMsgVO;
import com.hch.chat_simple.mapper.ChatMsgMapper;
import com.hch.chat_simple.service.IChatMsgService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.ContextUtil;

import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<ChatMsgVO> selectNotReadMsgMsg() {
        Wrapper<ChatMsgPO> query = Wrappers.<ChatMsgPO>query().lambda()
            .eq(ChatMsgPO::getReceiveUserId, ContextUtil.getUserId())
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
                    String dateTime = src.getCreatedAt().atZone(ZoneId.of(Constant.ZONED_SHANGHAI)).toInstant().toEpochMilli() + "";
                    // log.info("dateTime:{}", dateTime);
                    trg.setDateTime(dateTime);
                }
            });
    }
}
