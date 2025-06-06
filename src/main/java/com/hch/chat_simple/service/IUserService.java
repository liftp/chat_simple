package com.hch.chat_simple.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hch.chat_simple.pojo.dto.AddUserForm;
import com.hch.chat_simple.pojo.po.UserPO;
import com.hch.chat_simple.pojo.query.UserQuery;
import com.hch.chat_simple.pojo.vo.UserVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hch
 * @since 2024-11-06
 */
public interface IUserService extends IService<UserPO> {

    UserPO getUserByName(String username);

    List<UserVO> searchUserByName(UserQuery query);

    Boolean insertUser(AddUserForm form);

}
