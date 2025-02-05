package com.hch.chat_simple.mapper;

import com.hch.chat_simple.pojo.po.GroupMemberPO;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 群组成员 Mapper 接口
 * </p>
 *
 * @author hch
 * @since 2025-02-02
 */
@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMemberPO> {

}
