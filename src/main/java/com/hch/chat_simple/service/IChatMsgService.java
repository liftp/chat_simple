package com.hch.chat_simple.service;

import com.hch.chat_simple.pojo.po.ChatMsgPO;
import com.hch.chat_simple.pojo.vo.ChatMsgVO;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 聊天记录表 服务类
 * </p>
 *
 * @author hch
 * @since 2025-02-01
 */
public interface IChatMsgService extends IService<ChatMsgPO> {
    
    List<ChatMsgVO> selectNotReadMsgMsg();

}
