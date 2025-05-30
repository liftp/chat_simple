package com.hch.chat_simple.util;

public class Constant {
    

    public static final String NETTY_CHANNEL_CTX_PERMISSION = "permission";

    // 消息发送成功
    public static final Integer MSG_SEND_SUCCESSED = 1;
    // 消息发送成失败
    public static final Integer MSG_SEND_FAILED = 0;

    // 是否删除
    public static final Integer NOT_DELETE = 0;
    public static final Integer DELETED = 1;

    public static final String ZONED_SHANGHAI = "Asia/Shanghai";

    // 聊天类型
    public static final Integer SINGLE_CHAT = 1;
    public static final Integer MUILT_CHAT = 2;

    // 成员在群组中的状态 正常：1 离开：0
    public static final Integer IN_GROUP = 1;
    public static final Integer LEAVE_GROUP = 0;

    // redis 存储实例数量 key 
    public static final String INST_NUM_KEY = "INST_NUM";

    // redis 存储实例及映射关系key
    public static final String INST_WITH_MAP_KEY = "INST_WITH_MAP_KEY";
}
