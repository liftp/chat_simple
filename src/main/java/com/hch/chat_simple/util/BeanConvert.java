package com.hch.chat_simple.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.github.pagehelper.Page;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeanConvert {
    

    public static <O, T> T convert(O origin, Class<T> tClazz, BiConsumer<O, T> transformer) {
        T dest = null;
        try {
            dest = tClazz.getConstructor().newInstance();
        } catch (Exception e) {
            log.error("covert bean exception ", e);
        }
        BeanUtils.copyProperties(origin, dest);
        transformer.accept(origin, dest);
        return dest;
    }

    /**
     * 兼容PageHelper分页的列表转换
     * @param <O>           原始类型
     * @param <T>           目标类型
     * @param origin        原始列表对象
     * @param tClazz        目标类型
     * @param transformer   转换器
     * @return              转换结果
     */
    public static <O, T> List<T> convertList(List<O> origin, Class<T> tClazz, BiConsumer<O, T> transformer) {
        if (origin == null) {
            return null;
        } 
        else if (origin instanceof Page) {
            Page<O> page = (Page<O>) origin;
            Page<T> pageDest = new Page<>();
            pageDest.setPageNum(page.getPageNum());
            pageDest.setPageSize(page.getPageSize());
            pageDest.setPages(page.getPages());
            pageDest.setTotal(page.getTotal());
            page.getResult().stream()
                .forEach(e -> pageDest.add(convert(e, tClazz, transformer)));

            return pageDest;
        }
        return origin.stream()
            .map(e -> convert(e, tClazz, transformer))
            .collect(Collectors.toList());
    }

    public static <O, T> List<T> convert(List<O> origin, Class<T> tClazz) {
        return convertList(origin, tClazz, (a, b) -> {});
    }

    public static <O, T> T convertSingle(O origin, Class<T> tClazz) {
        return convert(origin, tClazz, (a, b) -> {});
    }



}
