package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.enums.ApplyStatusEnum;
import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.mapper.ApplyFriendMapper;
import com.hch.chat_simple.mq.AsyncProducer;
import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.po.ApplyFriendPO;
import com.hch.chat_simple.pojo.po.FriendRelationshipPO;
import com.hch.chat_simple.pojo.vo.ApplyFriendVO;
import com.hch.chat_simple.pojo.vo.ApplyResultInfoVO;
import com.hch.chat_simple.service.IApplyFriendService;
import com.hch.chat_simple.service.IFriendRelationshipService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 好友申请记录 服务实现类
 * </p>
 *
 * @author hch
 * @since 2025-02-14
 */
@Slf4j
@Service
public class ApplyFriendServiceImpl extends ServiceImpl<ApplyFriendMapper, ApplyFriendPO> implements IApplyFriendService {

    @Autowired
    private AsyncProducer asyncProducer;

    @Value("${mq.topic.composition}")
    private String compositionTopicName;

    @Autowired
    private IFriendRelationshipService iFriendRelationshipService;
    
    @Override
    public Long applyFriend(ApplyFriendDTO applyFriend) {
        Long userId = ContextUtil.getUserId();
        String userName = ContextUtil.getUsername();
        ApplyFriendPO po = BeanConvert.convertSingle(applyFriend, ApplyFriendPO.class);
        po.setProposerId(userId);
        po.setProposerName(userName);
        po.setApplyStatus(ApplyStatusEnum.APPLYING.getStatus());
        po.setCreatedAt(LocalDateTime.now());
        po.setCreatorBy(userName);
        po.setCreatorId(userId);
        po.setApplyRemark(applyFriend.getAppliedRemark());
        
        save(po);

        // 推送申请消息, 约定：消息类型+','+消息体，这样后续直接解析类型，之后再转对应的消息内容
        asyncProducer.asyncSend(compositionTopicName, MsgTypeEnum.APPLY_FRIEND.getType() + "," + po.getTargetUser() + "," + JSON.toJSONString(po));
        return po.getId();
    }

    @Override
    public Long applyFriendConfirm(ApplyFriendDTO applyFriend) {

        Long userId = ContextUtil.getUserId();
        String userName = ContextUtil.getUsername();
        // 被申请人保存申请记录
        ApplyFriendPO po = BeanConvert.convertSingle(applyFriend, ApplyFriendPO.class);
        po.setCreatedAt(LocalDateTime.now());
        po.setCreatorBy(userName);
        po.setCreatorId(userId);
        po.setApplyRemark(applyFriend.getAppliedRemark());
        // 通知申请者，好友申请确认结果
        ApplyResultInfoVO notify = new ApplyResultInfoVO();
        List<FriendRelationshipPO> ships = null;
        // 如果通过，添加两人的好友关系
        if (ApplyStatusEnum.APPLY_PASS.getStatus().equals(applyFriend.getApplyPass())) {
            // 查询申请的那条记录，取备注作为好友名称
            ApplyFriendPO initiatorRecord = selectApplyRecord(applyFriend.getProposerId(), userId);

            FriendRelationshipPO relateInitiator = new FriendRelationshipPO();
            relateInitiator.setFriendId(userId);
            relateInitiator.setFriendName(userName);
            relateInitiator.setFriendRemark(initiatorRecord.getApplyRemark());
            relateInitiator.setCreatorId(applyFriend.getProposerId());
            relateInitiator.setCreatorBy(initiatorRecord.getCreatorBy());

            FriendRelationshipPO relateTarget = new FriendRelationshipPO();
            relateTarget.setFriendId(applyFriend.getProposerId());
            relateTarget.setFriendName(applyFriend.getProposerName());
            relateTarget.setFriendRemark(applyFriend.getApplyRemark());
            relateTarget.setCreatorId(userId);
            relateTarget.setCreatorBy(userName);

            ships = Arrays.asList(relateInitiator, relateTarget);
            iFriendRelationshipService.saveBatch(ships);
            
            notify.setProposerRelationshipId(relateInitiator.getId());
            notify.setTargetRelationshipId(relateTarget.getId());
        }
        
        save(po);

        notify.setApplyStatus(applyFriend.getApplyPass());
        notify.setProposerId(po.getProposerId());
        notify.setProposerRemark(po.getProposerRemark());
        notify.setTargetUser(userId);

        // 发送给申请人 申请通过消息
        asyncProducer.asyncSend(compositionTopicName, MsgTypeEnum.APPLY_FRIEND_RESULT.getType() + "," + po.getProposerId() + "," + JSON.toJSONString(notify));
        // 发送给双方，添加好友关系的信息
        if (ships != null) {
            ships.forEach(s -> {
                asyncProducer.asyncSend(compositionTopicName, MsgTypeEnum.FRIEND_SHIP_ADD.getType() + "," + s.getCreatorId() + "," + JSON.toJSONString(s));
            });
        }
        
        return po.getId();
    }

    private ApplyFriendPO selectApplyRecord(Long applyUser, Long targetUser) {

        Wrapper<ApplyFriendPO> queryRecord = Wrappers.<ApplyFriendPO>query().lambda()
            .eq(ApplyFriendPO::getTargetUser, targetUser)
            .eq(ApplyFriendPO::getProposerId, applyUser)
            .orderByDesc(ApplyFriendPO::getCreatedAt)
            .last("limit 1");

        return getOne(queryRecord);
    }

    @Override
    public List<ApplyFriendVO> applyList(Long dataId) {
        // 查询当前用户的好友申请列表： 自己申请+被申请记录，按照创建时间倒序排列
        Long userId = ContextUtil.getUserId();
        if (dataId != null) {
            Wrapper<ApplyFriendPO> queryRecord = Wrappers.<ApplyFriendPO>query().lambda()
                .eq(ApplyFriendPO::getId, dataId)
                .eq(ApplyFriendPO::getCreatorId, userId)
                .orderByDesc(ApplyFriendPO::getCreatedAt)
                .last("limit 1");
            ApplyFriendPO lastOne = getOne(queryRecord);
            // 最后一条本地的时间，之后的所有的记录
            Wrapper<ApplyFriendPO> queryAfter = Wrappers.<ApplyFriendPO>query().lambda()
                .eq(ApplyFriendPO::getCreatorId, userId)
                .gt(ApplyFriendPO::getCreatedAt, lastOne.getCreatedAt())
                .orderByDesc(ApplyFriendPO::getCreatedAt);
            return BeanConvert.convert(list(queryAfter), ApplyFriendVO.class);
        }
        
        // 传参为空，拉取过去所有数据
        Wrapper<ApplyFriendPO> queryAfter = Wrappers.<ApplyFriendPO>query().lambda()
            .eq(ApplyFriendPO::getCreatorId, userId)
            .orderByDesc(ApplyFriendPO::getCreatedAt);
        return BeanConvert.convert(list(queryAfter), ApplyFriendVO.class);
    }

}
