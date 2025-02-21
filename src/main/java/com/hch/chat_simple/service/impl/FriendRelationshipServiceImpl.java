package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.po.FriendRelationshipPO;
import com.hch.chat_simple.pojo.po.UserPO;
import com.hch.chat_simple.pojo.query.FriendRelationshipQuery;
import com.hch.chat_simple.pojo.vo.FriendRelationshipVO;
import com.hch.chat_simple.exception.BusinessException;
import com.hch.chat_simple.mapper.FriendRelationshipMapper;
import com.hch.chat_simple.service.IFriendRelationshipService;
import com.hch.chat_simple.service.IUserService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;

import io.micrometer.common.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private IUserService iUserService;

    @Override
    public List<FriendRelationshipVO> listFriendRelationship(FriendRelationshipQuery query) {
        Long userId = ContextUtil.getUserId();
        // 找所属人是自己
        Wrapper<FriendRelationshipPO> queryFriends = Wrappers.<FriendRelationshipPO>query().lambda()
            .like(Integer.valueOf(1).equals(query.getSearchType()) && StringUtils.isNotBlank(query.getName()), FriendRelationshipPO::getFriendName, query.getName())
            .eq(Integer.valueOf(2).equals(query.getSearchType()) && query.getId() != null, FriendRelationshipPO::getId, query.getId())
            .eq(FriendRelationshipPO::getSelfId, userId);
        List<FriendRelationshipPO> poList = list(queryFriends);
        return BeanConvert.convert(poList, FriendRelationshipVO.class);
    }

    @Override
    @Transactional
    public void insertFriendRelationship(ApplyFriendDTO applyFriend) {
        // 申请通过
        // 校验是否存在双向完整好友关系，1.存在不操作，2.不完整（可能有乙方被拉黑）则删除原有的，重新添加
        Wrapper<FriendRelationshipPO> queryFriendship = Wrappers.<FriendRelationshipPO>query().lambda()
            .in(FriendRelationshipPO::getSelfId, applyFriend.getProposerId(), applyFriend.getTargetUser());
        List<FriendRelationshipPO> listFriends = list(queryFriendship);
        if (CollectionUtils.isNotEmpty(listFriends)) {
            if (listFriends.size() == 2) {
                return;
            } else {
                // 删除不完整的关系
                removeByIds(listFriends.stream().map(FriendRelationshipPO::getId).collect(Collectors.toList()));
            }
        }

        // 查询申请人和被申请人的信息
        Wrapper<UserPO> queryUser = Wrappers.<UserPO>query().lambda()
            .in(UserPO::getId, applyFriend.getProposerId(), applyFriend.getTargetUser());
        List<UserPO> list = iUserService.list(queryUser);
        UserPO applyUser = list.stream().filter(e -> e.getId().equals(applyFriend.getTargetUser()))
        .findAny().orElseThrow(() -> {
            return new BusinessException("申请人不存在");
        });
        UserPO appliedUser = list.stream().filter(e -> e.getId().equals(applyFriend.getTargetUser()))
        .findAny().orElseThrow(() -> {
            return new BusinessException("被申请人不存在");
        });
        LocalDateTime now = LocalDateTime.now();
        // 添加申请人的好友数据
        FriendRelationshipPO apply = new FriendRelationshipPO();
        apply.setFriendId(appliedUser.getId());
        apply.setFriendName(appliedUser.getUsername());
        apply.setFriendRemark(applyFriend.getAppliedRemark());
        apply.setSelfId(applyUser.getId());
        apply.setCreatedAt(now);
        apply.setCreatorBy(applyUser.getUsername());
        apply.setCreatorId(applyUser.getId());
        
        // 添加被申请人的好友数据
        FriendRelationshipPO applied = new FriendRelationshipPO();
        applied.setFriendId(applyUser.getId());
        applied.setFriendName(applyUser.getUsername());
        applied.setFriendRemark(applyFriend.getApplyRemark());
        applied.setSelfId(appliedUser.getId());
        applied.setCreatedAt(now);
        applied.setCreatorBy(appliedUser.getUsername());
        applied.setCreatorId(appliedUser.getId());

        saveBatch(Arrays.asList(apply, applied));
    }

    
}
