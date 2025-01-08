package com.hch.chat_simple.service;

import com.hch.chat_simple.pojo.po.FriendRelationshipPO;
import com.hch.chat_simple.pojo.vo.FriendRelationshipVO;
import com.hch.chat_simple.util.PageBean;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 朋友关系表 服务类
 * </p>
 *
 * @author hch
 * @since 2024-12-06
 */
public interface IFriendRelationshipService extends IService<FriendRelationshipPO> {

    List<FriendRelationshipVO> listFriendRelationship();
}
