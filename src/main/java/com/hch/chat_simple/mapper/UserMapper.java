package com.hch.chat_simple.mapper;



import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hch.chat_simple.pojo.po.UserPO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hch
 * @since 2024-11-06
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

}
