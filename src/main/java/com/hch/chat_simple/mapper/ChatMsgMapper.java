package com.hch.chat_simple.mapper;

import com.hch.chat_simple.pojo.po.ChatMsgPO;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 聊天记录表 Mapper 接口
 * </p>
 *
 * @author hch
 * @since 2025-02-01
 */
@Mapper
public interface ChatMsgMapper extends BaseMapper<ChatMsgPO> {

}
