package com.hch.chat_simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hch.chat_simple.pojo.po.UserPO;

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

}
