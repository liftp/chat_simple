package com.hch.chat_simple.config;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hch.chat_simple.util.Constant;
import com.hch.chat_simple.util.ContextUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrmMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "creatorId", Long.class, ContextUtil.getUserId());
        this.strictInsertFill(metaObject, "creatorBy", String.class, ContextUtil.getUsername());
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "dr", Integer.class, Constant.NOT_DELETE);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "modifierId", Long.class, ContextUtil.getUserId());
        this.strictUpdateFill(metaObject, "modifierBy", String.class, ContextUtil.getUsername());
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
    
}
