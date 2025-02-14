package com.hch.chat_simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.po.ApplyFriendPO;

/**
 * <p>
 * 好友申请记录 服务类
 * </p>
 *
 * @author hch
 * @since 2025-02-14
 */
public interface IApplyFriendService extends IService<ApplyFriendPO> {

    void applyFriend(ApplyFriendDTO applyFriend);
}
