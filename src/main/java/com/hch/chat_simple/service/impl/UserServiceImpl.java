package com.hch.chat_simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hch.chat_simple.mapper.FriendRelationshipMapper;
import com.hch.chat_simple.mapper.UserMapper;
import com.hch.chat_simple.pojo.dto.AddUserForm;
import com.hch.chat_simple.pojo.po.FriendRelationshipPO;
import com.hch.chat_simple.pojo.po.UserPO;
import com.hch.chat_simple.pojo.query.UserQuery;
import com.hch.chat_simple.pojo.vo.UserVO;
import com.hch.chat_simple.service.IFriendRelationshipService;
import com.hch.chat_simple.service.IUserService;
import com.hch.chat_simple.util.BeanConvert;
import com.hch.chat_simple.util.ContextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hch
 * @since 2024-11-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO> implements IUserService {

    @Autowired
    private FriendRelationshipMapper friendRelationshipMapper;
    @Override
    public UserPO getUserByName(String username) {
        return baseMapper.selectOne(Wrappers.<UserPO>query().lambda()
            .eq(UserPO::getUsername, username),
            false
        );
    }

    @Override
    public List<UserVO> searchUserByName(UserQuery query) {
        List<UserPO> userList = baseMapper.selectList(Wrappers.<UserPO>query().lambda()
            .like(UserPO::getUsername, query.getUsername())
            .last("limit 20")
        );
        
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        List<UserVO> result = BeanConvert.convert(userList, UserVO.class);
        // 查询人员是不是当前人的好友，增加标识
        if (CollectionUtils.isNotEmpty(userList)) {
            List<Long> userIds = userList.stream().map(UserPO::getId).collect(Collectors.toList());
            Wrapper<FriendRelationshipPO> queryFriend = Wrappers.<FriendRelationshipPO>query().lambda()
                .in(FriendRelationshipPO::getFriendId, userIds)
                .eq(FriendRelationshipPO::getSelfId, ContextUtil.getUserId());
            List<FriendRelationshipPO> friends = friendRelationshipMapper.selectList(queryFriend);
            if (CollectionUtils.isNotEmpty(friends)) {
                Set<Long> friendsIds = friends.stream().map(FriendRelationshipPO::getFriendId).collect(Collectors.toSet());
                result.forEach(e -> {
                    e.setFriendRelation(friendsIds.contains(e.getId()));
                });
            } else {
                result.forEach(e -> {
                    e.setFriendRelation(false);
                });
            }
        }
        return result;
    }

    @Override
    public Boolean insertUser(AddUserForm form) {
        // 校验添加用户登录名是否存在
        Wrapper<UserPO> query = Wrappers.<UserPO>query().lambda()
            .eq(UserPO::getUsername, form.getUsername());
        long count =  this.count(query);
        if (count > 0) {
            throw new RuntimeException("登录名已存在");
        }
        UserPO userPO = BeanConvert.convertSingle(form, UserPO.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String pwd = passwordEncoder.encode(userPO.getPassword());
        userPO.setPassword(pwd);
        save(userPO);
        return true;
    }

}
