package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.pojo.po.ChatMsgPO;
import com.hch.chat_simple.mapper.ChatMsgMapper;
import com.hch.chat_simple.service.IChatMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class ChatMsgServiceImpl extends ServiceImpl<ChatMsgMapper, ChatMsgPO> implements IChatMsgService {

}
