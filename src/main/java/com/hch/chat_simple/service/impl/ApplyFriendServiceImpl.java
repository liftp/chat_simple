package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.enums.MsgTypeEnum;
import com.hch.chat_simple.mapper.ApplyFriendMapper;
import com.hch.chat_simple.mq.AsyncProducer;
import com.hch.chat_simple.pojo.dto.ApplyFriendDTO;
import com.hch.chat_simple.pojo.po.ApplyFriendPO;
import com.hch.chat_simple.pojo.vo.ApplyFriendVO;
import com.hch.chat_simple.service.IApplyFriendService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
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
    
    @Override
    public Long applyFriend(ApplyFriendDTO applyFriend) {
        Long userId = ContextUtil.getUserId();
        String userName = ContextUtil.getUsername();
        ApplyFriendPO po = BeanConvert.convertSingle(applyFriend, ApplyFriendPO.class);
        po.setProposerId(userId);
        po.setProposerName(userName);
        po.setCreatedAt(LocalDateTime.now());
        po.setCreatorBy(userName);
        po.setCreatorId(userId);
        
        save(po);

        // 推送申请消息, 约定：消息类型+','+消息体，这样后续直接解析类型，之后再转对应的消息内容
        asyncProducer.asyncSend(compositionTopicName, MsgTypeEnum.APPLY_FRIEND.getType() + "," + po.getTargetUser() + "," + JSON.toJSONString(po));
        return po.getId();
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
