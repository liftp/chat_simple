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
import com.hch.chat_simple.util.InstanceMapTagUtils;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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
    public ApplyFriendVO applyFriend(ApplyFriendDTO applyFriend) {
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
        // 调整或新增记录更新时间，查找申请记录的时候根据修改时间往后查找
        po.setUpdatedAt(LocalDateTime.now());

        // 被申请人记录
        ApplyFriendPO applied = BeanConvert.convertSingle(po, ApplyFriendPO.class);
        applied.setApplyRemark("");
        applied.setCreatorId(po.getTargetUser());
        applied.setCreatorBy(" ");
        
        saveBatch(Arrays.asList(po, applied));

        // 推送申请消息, 约定：消息类型+','+消息体，这样后续直接解析类型，之后再转对应的消息内容
        int tag = InstanceMapTagUtils.singleIdMapTag(po.getTargetUser());

        asyncProducer.asyncSend(compositionTopicName, tag + "", MsgTypeEnum.APPLY_FRIEND.getType() + "," + po.getTargetUser() + "," + JSON.toJSONString(po));

        ApplyFriendVO vo = BeanConvert.convertSingle(po, ApplyFriendVO.class);
        vo.setUpdateTime(po.getUpdatedAt().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
        vo.setApplyPass(po.getApplyStatus());
        return vo;
    }

    @Override
    public ApplyFriendVO applyFriendConfirm(ApplyFriendDTO applyFriend) {

        Long userId = ContextUtil.getUserId();
        String userName = ContextUtil.getUsername();
        // 通知申请者，好友申请确认结果
        ApplyResultInfoVO notify = new ApplyResultInfoVO();
        List<FriendRelationshipPO> ships = null;
        // 如果通过，添加两人的好友关系
        List<ApplyFriendPO> initiatorRecord = null;            // 申请人记录
        ApplyFriendPO applyRecord = null;
        // 被申请人记录
        ApplyFriendPO appliedRecord = null;
        // 查询申请的那条记录，取备注作为好友名称
        initiatorRecord = selectApplyRecordForTarget(applyFriend.getProposerId(), userId);


        for (ApplyFriendPO record : initiatorRecord) {
            if (record.getCreatorId() == userId) {
                appliedRecord = record;
            } else {
                applyRecord = record;
            }
        }
        if (ApplyStatusEnum.APPLY_PASS.getStatus().equals(applyFriend.getApplyPass())) {

            FriendRelationshipPO relateInitiator = new FriendRelationshipPO();
            relateInitiator.setFriendId(userId);
            relateInitiator.setFriendName(userName);
            relateInitiator.setFriendRemark(applyRecord.getApplyRemark());
            relateInitiator.setCreatorId(applyFriend.getProposerId());
            relateInitiator.setCreatorBy(applyRecord.getCreatorBy());
            relateInitiator.setSelfId(applyFriend.getProposerId());

            FriendRelationshipPO relateTarget = new FriendRelationshipPO();
            relateTarget.setFriendId(applyFriend.getProposerId());
            relateTarget.setFriendName(applyFriend.getProposerName());
            relateTarget.setFriendRemark(applyFriend.getAppliedRemark());
            relateTarget.setCreatorId(userId);
            relateTarget.setCreatorBy(userName);
            relateTarget.setSelfId(userId);

            ships = Arrays.asList(relateInitiator, relateTarget);
            iFriendRelationshipService.saveBatch(ships);
            
            notify.setProposerRelationshipId(relateInitiator.getId());
            notify.setTargetRelationshipId(relateTarget.getId());

            
        }
        // 更新申请人记录状态
        applyRecord.setApplyStatus(applyFriend.getApplyPass());
        applyRecord.setUpdatedAt(LocalDateTime.now());
        appliedRecord.setApplyStatus(applyFriend.getApplyPass());
        appliedRecord.setUpdatedAt(LocalDateTime.now());
        appliedRecord.setCreatorBy(ContextUtil.getUsername());
        appliedRecord.setApplyRemark(applyFriend.getAppliedRemark());
        updateBatchById(initiatorRecord);


        notify.setApplyStatus(applyFriend.getApplyPass());
        notify.setProposerId(appliedRecord.getProposerId());
        notify.setProposerRemark(appliedRecord.getProposerRemark());
        notify.setTargetUser(userId);

        // 发送给申请人 申请通过消息
        int tag = InstanceMapTagUtils.singleIdMapTag(appliedRecord.getProposerId());
        asyncProducer.asyncSend(compositionTopicName, tag + "", MsgTypeEnum.APPLY_FRIEND_RESULT.getType() + "," + appliedRecord.getProposerId() + "," + JSON.toJSONString(notify));

        // 发送给双方，添加好友关系的信息
        if (ships != null) {
            ships.forEach(s -> {
                int tag1 = InstanceMapTagUtils.singleIdMapTag(s.getCreatorId());
                asyncProducer.asyncSend(compositionTopicName, tag1 + "", MsgTypeEnum.FRIEND_SHIP_ADD.getType() + "," + s.getCreatorId() + "," + JSON.toJSONString(s));

            });
        }
        
        
        ApplyFriendVO vo = BeanConvert.convertSingle(appliedRecord, ApplyFriendVO.class);

        vo.setUpdateTime(appliedRecord.getUpdatedAt().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
        return vo;
    }

    private List<ApplyFriendPO> selectApplyRecordForTarget(Long applyUser, Long targetUser) {

        Wrapper<ApplyFriendPO> queryRecord = Wrappers.<ApplyFriendPO>query().lambda()
            .eq(ApplyFriendPO::getTargetUser, targetUser)
            .eq(ApplyFriendPO::getProposerId, applyUser)
            .orderByDesc(ApplyFriendPO::getCreatedAt)
            .last("limit 2");

        return list(queryRecord);
    }

    @Override
    public List<ApplyFriendVO> applyList(Long updateLast) {
        // 查询当前用户的好友申请列表： 自己申请+被申请记录，按照创建时间倒序排列
        Long userId = ContextUtil.getUserId();
        // if (updateLast != null) {
        //     Wrapper<ApplyFriendPO> queryRecord = Wrappers.<ApplyFriendPO>query().lambda()
        //         // 根据更新时间往后查询最新的申请记录
        //         .ge(ApplyFriendPO::getUpdatedAt, LocalDateTime.ofInstant(Instant.ofEpochMilli(updateLast), ZoneOffset.ofHours(8)))
        //         .eq(ApplyFriendPO::getCreatorId, userId)
        //         .orderByDesc(ApplyFriendPO::getCreatedAt)
        //         .last("limit 1");
        //     ApplyFriendPO lastOne = getOne(queryRecord);
        //     // 最后一条本地的时间，之后的所有的记录
        //     Wrapper<ApplyFriendPO> queryAfter = Wrappers.<ApplyFriendPO>query().lambda()
        //         .eq(ApplyFriendPO::getCreatorId, userId)
        //         .gt(ApplyFriendPO::getCreatedAt, lastOne.getCreatedAt())
        //         .orderByDesc(ApplyFriendPO::getCreatedAt);
        //     return BeanConvert.convert(list(queryAfter), ApplyFriendVO.class);
        // }
        
        Supplier<LocalDateTime> supDate = () -> updateLast != null ? 
            LocalDateTime.ofInstant(Instant.ofEpochMilli(updateLast), ZoneOffset.ofHours(8)) 
            : LocalDateTime.now();
        // 传参为空，拉取过去所有数据
        Wrapper<ApplyFriendPO> queryAfter = Wrappers.<ApplyFriendPO>query().lambda()
            .ge(updateLast != null, ApplyFriendPO::getUpdatedAt, supDate.get())
            .eq(ApplyFriendPO::getCreatorId, userId)
            .orderByDesc(ApplyFriendPO::getCreatedAt);
        
        List<ApplyFriendVO> applyRecords = BeanConvert.convertList(list(queryAfter), ApplyFriendVO.class,
            (src, trg) -> {
                trg.setApplyPass(src.getApplyStatus());
                trg.setUpdateTime(src.getUpdatedAt().toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
            }
        );
        return applyRecords;
    }

}
