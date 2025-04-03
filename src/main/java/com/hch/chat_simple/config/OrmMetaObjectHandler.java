package com.hch.chat_simple.config;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hch.chat_simple.util.ContextUtil;

public class OrmMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "creatorId", Long.class, ContextUtil.getUserId());
        this.strictInsertFill(metaObject, "creatorBy", String.class, ContextUtil.getUsername());
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "modifierId", Long.class, ContextUtil.getUserId());
        this.strictInsertFill(metaObject, "modifierBy", String.class, ContextUtil.getUsername());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
    
}
