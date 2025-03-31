package com.hch.chat_simple.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.vo.ApplyFriendVO;
import com.hch.chat_simple.service.IApplyFriendService;
import com.hch.chat_simple.util.Payload;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

/**
 * <p>
 * 好友申请记录 前端控制器
 * </p>
 *
 * @author hch
 * @since 2025-02-14
 */
@RestController
@RequestMapping("/applyFriend/")
public class ApplyFriendController {
    @Autowired
    private IApplyFriendService iAppFriendService;


    @PostMapping("applyRecord")
    @Operation(description = "好友申请记录列表")
    public Payload<List<ApplyFriendVO>> applyRecord(@RequestParam(required = false) Long dataId) {
        return Payload.success(iAppFriendService.applyList(dataId));
    }

    @PostMapping("applyFriend")
    @Operation(description = "好友申请")
    public Payload<Long> applyFriend(@Valid @RequestBody ApplyFriendDTO dto) {
        return Payload.success(iAppFriendService.applyFriend(dto));
    }

    
}
