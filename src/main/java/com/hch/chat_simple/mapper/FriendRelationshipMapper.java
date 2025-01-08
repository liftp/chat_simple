package com.hch.chat_simple.mapper;

import com.hch.chat_simple.pojo.po.FriendRelationshipPO;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 朋友关系表 Mapper 接口
 * </p>
 *
 * @author hch
 * @since 2024-12-06
 */
@Mapper
public interface FriendRelationshipMapper extends BaseMapper<FriendRelationshipPO> {

}
