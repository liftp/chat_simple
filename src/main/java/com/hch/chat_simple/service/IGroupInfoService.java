package com.hch.chat_simple.service;

import com.hch.chat_simple.pojo.dto.GroupInfoDTO;
import com.hch.chat_simple.pojo.po.GroupInfoPO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 群组信息 服务类
 * </p>
 *
 * @author hch
 * @since 2025-02-02
 */
public interface IGroupInfoService extends IService<GroupInfoPO> {

    boolean addGroupChat(GroupInfoDTO dto);

}
