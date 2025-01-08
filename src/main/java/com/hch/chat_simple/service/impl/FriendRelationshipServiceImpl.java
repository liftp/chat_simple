package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.pojo.po.FriendRelationshipPO;
import com.hch.chat_simple.pojo.vo.FriendRelationshipVO;
import com.hch.chat_simple.mapper.FriendRelationshipMapper;
import com.hch.chat_simple.service.IFriendRelationshipService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 朋友关系表 服务实现类
 * </p>
 *
 * @author hch
 * @since 2024-12-06
 */
@Service
public class FriendRelationshipServiceImpl extends ServiceImpl<FriendRelationshipMapper, FriendRelationshipPO> implements IFriendRelationshipService {

    @Override
    public List<FriendRelationshipVO> listFriendRelationship() {
        Long userId = ContextUtil.getUserId();
        // 找所属人是自己
        Wrapper<FriendRelationshipPO> queryFriends = Wrappers.<FriendRelationshipPO>query().lambda()
            .eq(FriendRelationshipPO::getSelfId, userId);
        List<FriendRelationshipPO> poList = list(queryFriends);
        return BeanConvert.convert(poList, FriendRelationshipVO.class);
    }

    
}
