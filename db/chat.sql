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
    self_id DATETIME COMMENT '好友关系所属id',
    creator_id BIGINT COMMENT '创建人id',
    creator_by VARCHAR(64) COMMENT '创建人姓名',
    updated_at DATETIME COMMENT '修改时间',
    modifier_id BIGINT COMMENT '修改人id',
    modifier_by VARCHAR(64) COMMENT '修改人姓名',
    dr TINYINT(1) COMMENT '是否删除 0:未删除 1:已删除'
) COMMENT '朋友关系表';

