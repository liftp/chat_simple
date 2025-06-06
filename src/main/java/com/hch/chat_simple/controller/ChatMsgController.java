package com.hch.chat_simple.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hch.chat_simple.auth.NoAuth;
import com.hch.chat_simple.pojo.dto.ChatMsgDTO;
import com.hch.chat_simple.pojo.query.GroupNotReadMsgQuery;
import com.hch.chat_simple.pojo.vo.ChatMsgVO;
import com.hch.chat_simple.service.IChatMsgService;
import com.hch.chat_simple.util.Payload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * <p>
 * 聊天记录表 前端控制器
 * </p>
 *
 * @author hch
 * @since 2025-02-01
 */
@RestController
@RequestMapping("/chatMsg/")
@Tag(name = "聊天消息")
public class ChatMsgController {

    @Resource
    private IChatMsgService iChatMsgService;

    @PostMapping("selectNotReadMsg")
    @Operation(description = "未读消息拉取")
    public Payload<List<ChatMsgVO>> selectNotReadMsg() {
        return Payload.success(iChatMsgService.selectNotReadMsgMsg());
    }

    @PostMapping("sendMsg")
    @Operation(description = "发送消息")
    public Payload<ChatMsgVO> sendMsg(@RequestBody ChatMsgDTO msg) {
        return Payload.success(iChatMsgService.sendMsg(msg));
    }

    @PostMapping("selectGroupChatMsgNotRead")
    @Operation(description = "群聊查询未读消息")
    public Payload<List<ChatMsgVO>> selectGroupChatMsgNotRead(@RequestBody GroupNotReadMsgQuery query) {
        return Payload.success(iChatMsgService.selectGroupChatMsgNotRead(query));
    }
    
}
