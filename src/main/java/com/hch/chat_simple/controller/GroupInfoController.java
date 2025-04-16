package com.hch.chat_simple.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hch.chat_simple.pojo.dto.GroupInfoDTO;
import com.hch.chat_simple.service.IGroupInfoService;
import com.hch.chat_simple.util.Payload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <p>
 * 群组信息 前端控制器
 * </p>
 *
 * @author hch
 * @since 2025-02-02
 */
@RestController
@RequestMapping("/groupInfo/")
@Tag(name = "群组操作")
public class GroupInfoController {

    @Autowired
    private IGroupInfoService iGroupInfoService;


    @PostMapping("addGroupChat")
    @Operation(description = "添加群聊")
    public Payload addGroupChat(@RequestBody GroupInfoDTO dto) {

        return Payload.success(iGroupInfoService.addGroupChat(dto));
    }

}
