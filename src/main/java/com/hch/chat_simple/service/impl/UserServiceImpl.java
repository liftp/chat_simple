package com.hch.chat_simple.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hch.chat_simple.mapper.UserMapper;
import com.hch.chat_simple.pojo.po.UserPO;
import com.hch.chat_simple.service.IUserService;

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

    @Override
    public UserPO getUserByName(String username) {
        return baseMapper.selectOne(Wrappers.<UserPO>query().lambda()
            .eq(UserPO::getUsername, username),
            false
        );
    }

}
