package com.hch.chat_simple.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.po.ApplyFriendPO;
import com.hch.chat_simple.pojo.vo.ApplyFriendVO;

/**
 * <p>
 * 好友申请记录 服务类
 * </p>
 *
 * @author hch
 * @since 2025-02-14
 */
public interface IApplyFriendService extends IService<ApplyFriendPO> {

    Long applyFriend(ApplyFriendDTO applyFriend);

    List<ApplyFriendVO> applyList(Long dataId);
}
