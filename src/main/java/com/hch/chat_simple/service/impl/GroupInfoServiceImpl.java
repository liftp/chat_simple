package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.pojo.dto.GroupInfoDTO;
import com.hch.chat_simple.pojo.po.GroupInfoPO;
import com.hch.chat_simple.pojo.po.GroupMemberPO;
import com.hch.chat_simple.mapper.GroupInfoMapper;
import com.hch.chat_simple.service.IGroupInfoService;
import com.hch.chat_simple.service.IGroupMemberService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 群组信息 服务实现类
 * </p>
 *
 * @author hch
 * @since 2025-02-02
 */
@Service
public class GroupInfoServiceImpl extends ServiceImpl<GroupInfoMapper, GroupInfoPO> implements IGroupInfoService {

    @Autowired
    private IGroupMemberService iGroupMemberService;

    @Override
    @Transactional
    public boolean addGroupChat(GroupInfoDTO dto) {
        Long userId = ContextUtil.getUserId();
        String uesrname = ContextUtil.getUsername();
        GroupInfoPO po = BeanConvert.convertSingle(dto, GroupInfoPO.class);
        po.setSelfId(ContextUtil.getUserId());
        
        save(po);
        
        // 添加群聊所属作为一个群成员
        GroupMemberPO memberPO = new GroupMemberPO();
        memberPO.setGroupId(po.getId());
        memberPO.setMemberId(userId);
        memberPO.setMemberName(uesrname);
        memberPO.setMemberRemark(uesrname);
        memberPO.setInviteId(userId);
        
        iGroupMemberService.save(memberPO);
        return true;
    }
}
