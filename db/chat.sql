CREATE DATABASE chat
    DEFAULT CHARACTER SET = 'utf8mb4';
use chat;

CREATE TABLE user (  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    
    username VARCHAR(64) COMMENT '用户名',
    password VARCHAR(64) COMMENT '密码',
    name VARCHAR(64) COMMENT '真实姓名',
    created_at DATETIME COMMENT '创建时间',
    creator_id BIGINT COMMENT '创建人id',
    creator_by VARCHAR(64) COMMENT '创建人姓名',
    updated_at DATETIME COMMENT '修改时间',
    modifier_id BIGINT COMMENT '修改人id',
    modifier_by VARCHAR(64) COMMENT '修改人姓名',
    dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
) COMMENT '用户表';

CREATE TABLE friend_relationship (  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    
    friend_id BIGINT COMMENT '朋友id',
    friend_name VARCHAR(64) COMMENT '朋友名称',
    friend_remark VARCHAR(64) COMMENT '朋友备注',
    self_id BIGINT COMMENT '好友关系所属id',
    creator_id BIGINT COMMENT '创建人id',
    creator_by VARCHAR(64) COMMENT '创建人姓名',
    updated_at DATETIME COMMENT '修改时间',
    modifier_id BIGINT COMMENT '修改人id',
    modifier_by VARCHAR(64) COMMENT '修改人姓名',
    dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
) COMMENT '朋友关系表';


CREATE TABLE chat_msg (  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    
    msg_type TINYINT(1) COMMENT '消息类型 1:上线 2:聊天 3:下线',
    chat_type TINYINT(1) COMMENT '聊天类型 0:单聊 1:群聊',
    send_user_id BIGINT COMMENT '发送人',
    receive_user_id BIGINT COMMENT '接收人，群聊时为空',
    `content` VARCHAR(1000) COMMENT '消息内容',
    group_id BIGINT COMMENT '群聊id',
    `status` TINYINT(1) COMMENT '群聊消息发送状态 0:失败 1:成功',
    created_at DATETIME(3) COMMENT '创建时间',
    creator_id BIGINT COMMENT '创建人id',
    creator_by VARCHAR(64) COMMENT '创建人姓名',
    updated_at DATETIME COMMENT '修改时间',
    modifier_id BIGINT COMMENT '修改人id',
    modifier_by VARCHAR(64) COMMENT '修改人姓名',
    dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
) COMMENT '聊天记录表';

CREATE TABLE group_info (  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
    
    group_name VARCHAR(64) COMMENT '群组名称',
    group_remark VARCHAR(64) COMMENT '群组信息',
    group_status TINYINT(4) COMMENT '群状态： 0:开放 1:成员邀请加入 2:仅所属人拉取 3:密码进入',
    group_lock_pwd VARCHAR(64) COMMENT '群进入密码',
    self_id BIGINT COMMENT '群组所属人id',
    created_at DATETIME COMMENT '创建时间',
    creator_id BIGINT COMMENT '创建人id',
    creator_by VARCHAR(64) COMMENT '创建人姓名',
    updated_at DATETIME COMMENT '修改时间',
    modifier_id BIGINT COMMENT '修改人id',
    modifier_by VARCHAR(64) COMMENT '修改人姓名',
    dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
) COMMENT '群组信息';

CREATE TABLE group_member (  
    id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',

    group_id BIGINT COMMENT '群组id',
    member_id BIGINT COMMENT '好友id',
    member_name VARCHAR(64) COMMENT '好友名称',
    member_remark VARCHAR(64) COMMENT '群内备注',
    invite_id BIGINT COMMENT '邀请人',
    `status` tinyint(1) DEFAULT '1' COMMENT '成员群组中状态 0: 在群聊中 1: 离开群聊',
    created_at DATETIME COMMENT '创建时间',
    creator_id BIGINT COMMENT '创建人id',
    creator_by VARCHAR(64) COMMENT '创建人姓名',
    updated_at DATETIME COMMENT '修改时间',
    modifier_id BIGINT COMMENT '修改人id',
    modifier_by VARCHAR(64) COMMENT '修改人姓名',
    dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
) COMMENT '群组成员';


CREATE TABLE apply_friend (  
	id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
	proposer_id BIGINT COMMENT '申请人id',
	proposer_name BIGINT COMMENT '申请人名称',
	proposer_remark VARCHAR(64) COMMENT '申请人备注',
	proposer_reason VARCHAR(64) COMMENT '申请理由',
	target_user BIGINT COMMENT '被申请好友id', 
	apply_status TINYINT(4) COMMENT '申请状态： 0:申请中 1:通过 2:拒绝',
    `apply_remark` varchar(64) DEFAULT NULL COMMENT '给好友的备注，用于后面的好友展示',
	created_at DATETIME COMMENT '创建时间',
	creator_id BIGINT COMMENT '创建人id',
	creator_by VARCHAR(64) COMMENT '创建人姓名',
	updated_at DATETIME COMMENT '修改时间',
	modifier_id BIGINT COMMENT '修改人id',
	modifier_by VARCHAR(64) COMMENT '修改人姓名',
	dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
) COMMENT '好友申请记录'


CREATE TABLE `notify_msg`  (
    `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `content` varchar(1000) NULL COMMENT '消息内容',
    `status` tinyint(1) NULL COMMENT '群聊消息发送状态 0:失败 1:成功',
    created_at DATETIME COMMENT '创建时间',
    creator_id BIGINT COMMENT '创建人id',
    creator_by VARCHAR(64) COMMENT '创建人姓名',
    updated_at DATETIME COMMENT '修改时间',
    modifier_id BIGINT COMMENT '修改人id',
    modifier_by VARCHAR(64) COMMENT '修改人姓名',
    dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
    PRIMARY KEY (`id`)
) COMMENT = '通知类型消息';