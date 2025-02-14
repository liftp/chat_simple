package com.hch.chat_simple.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hch.chat_simple.pojo.po.ApplyFriendPO;

/**
 * <p>
 * 好友申请记录 Mapper 接口
 * </p>
 *
 * @author hch
 * @since 2025-02-14
 */
@Mapper
public interface ApplyFriendMapper extends BaseMapper<ApplyFriendPO> {

}
