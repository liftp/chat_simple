package com.hch.chat_simple.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.query.FriendRelationshipQuery;
import com.hch.chat_simple.pojo.vo.FriendRelationshipVO;
import com.hch.chat_simple.service.IApplyFriendService;
import com.hch.chat_simple.service.IFriendRelationshipService;
import com.hch.chat_simple.util.Payload;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

/**
 * <p>
 * 朋友关系表 前端控制器
 * </p>
 *
 * @author hch
 * @since 2024-12-06
 */
@RestController
@RequestMapping("/friendship")
@AllArgsConstructor
public class FriendRelationshipController {

    private final IFriendRelationshipService iFriendRelationshipService;

    private final IApplyFriendService iApplyFriendService;

    @PostMapping("/friendList")
    @Operation(description = "好友列表")
    public Payload<List<FriendRelationshipVO>> friendList(@RequestBody FriendRelationshipQuery query) {
        return Payload.success(iFriendRelationshipService.listFriendRelationship(query));
    }

    @PostMapping("/applyFriend")
    @Operation(description = "好友申请")
    public Payload applyFriend(ApplyFriendDTO applyFriend) {
        return Payload.success(true);
    }
}
