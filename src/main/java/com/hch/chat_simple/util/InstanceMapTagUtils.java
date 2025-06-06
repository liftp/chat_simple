package com.hch.chat_simple.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class InstanceMapTagUtils {

    @Value("${chat.tag.list}")
    private String instanceWithTag;

    public InstanceMapTagUtils() {
        
    }

    @PostConstruct
    public void setInstanceSize() {
        InnerInstanceTagSize.instanceSize = instanceWithTag.split(",").length;
    }
    
    // public static Map<String, String> getCacheInstanceInfo() {
    //     return RedisUtil.mapGetAll(Constant.INST_WITH_MAP_KEY);
    // }

    public static int singleIdMapTag(Long id) {
        // 例如实例3个，用户id: 1%3 = 1, 2%3=2, 3%3=0 => 3,tag有三个1,2,3
        // Map<String, String> instanceWithTag = getCacheInstanceInfo();
        int size = InnerInstanceTagSize.instanceSize;
        int mod = id.intValue() % size;
        return  mod == 0 ? size : mod;
    }

    public static Map<Integer, List<Long>> multiGroupByTag(List<Long> ids) {
        // Map<String, String> instanceWithTag = getCacheInstanceInfo();
        int size = InnerInstanceTagSize.instanceSize;
        return ids.stream().collect(Collectors.groupingBy(e -> {
            int mod = e.intValue() % size;
            return mod == 0 ? size : mod;
        }));
    }

    
    public class InnerInstanceTagSize {
        private static int instanceSize;
        // private List<String> instanceWithTag;
    }
}
