package com.hch.chat_simple.service.impl;

import com.hch.chat_simple.pojo.dto.AddGroupMembersDTO;
import com.hch.chat_simple.pojo.dto.GroupInfoDTO;
import com.hch.chat_simple.pojo.po.GroupInfoPO;
import com.hch.chat_simple.pojo.po.GroupMemberPO;
import com.hch.chat_simple.pojo.po.UserPO;
import com.hch.chat_simple.pojo.vo.GroupInfoVO;
import com.hch.chat_simple.pojo.vo.GroupMemberVO;
import com.hch.chat_simple.mapper.GroupInfoMapper;
import com.hch.chat_simple.service.IGroupInfoService;
import com.hch.chat_simple.service.IGroupMemberService;
import com.hch.chat_simple.service.IUserService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    private IUserService iUserService;

    @Override
    @Transactional
    public GroupInfoVO addGroupChat(GroupInfoDTO dto) {
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
        return BeanConvert.convertSingle(po, GroupInfoVO.class);
    }

    @Override
    public List<GroupMemberVO> findGroupMemberById(Long groupId) {
        Wrapper<GroupMemberPO> queryMember = Wrappers.<GroupMemberPO>query().lambda()
            .eq(GroupMemberPO::getGroupId, groupId);
        List<GroupMemberPO> members = iGroupMemberService.list(queryMember);
        
        return BeanConvert.convert(members, GroupMemberVO.class);
    }

    @Override
    @Transactional
    public boolean addGroupMembers(AddGroupMembersDTO dto) {
        Wrapper<UserPO> queryUserInfo = Wrappers.<UserPO>query().lambda()
            .in(UserPO::getId, dto.getUserIds());
        List<UserPO> userList = iUserService.list(queryUserInfo);
        Map<Long, UserPO> userIdMap  = userList.stream().collect(Collectors.toMap(UserPO::getId, Function.identity()));
        // 查询group已有的成员，添加时过滤
        List<GroupMemberVO> havedMembers = findGroupMemberById(dto.getGroupId());
        Set<Long> memberIdsFilter = havedMembers.stream().map(e -> e.getId()).collect(Collectors.toSet());
        List<GroupMemberPO> insertList = dto.getUserIds().stream()
            .filter(e -> !memberIdsFilter.contains(e))
            .map(e -> {
                GroupMemberPO po = new GroupMemberPO();
                po.setMemberId(e);
                po.setGroupId(dto.getGroupId());
                po.setMemberName(userIdMap.get(e).getUsername());
                return po;
            }).collect(Collectors.toList());
        iGroupMemberService.saveBatch(insertList);
        return true;
    }
}
