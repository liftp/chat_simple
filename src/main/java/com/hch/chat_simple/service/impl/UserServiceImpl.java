package com.hch.chat_simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hch.chat_simple.mapper.UserMapper;
import com.hch.chat_simple.pojo.dto.AddUserForm;
import com.hch.chat_simple.pojo.po.UserPO;
import com.hch.chat_simple.pojo.query.UserQuery;
import com.hch.chat_simple.pojo.vo.UserVO;
import com.hch.chat_simple.service.IUserService;
import com.hch.chat_simple.util.BeanConvert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
        );
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return BeanConvert.convert(userList, UserVO.class);
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
