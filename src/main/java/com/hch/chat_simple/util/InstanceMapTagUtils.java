package com.hch.chat_simple.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InstanceMapTagUtils {
    
    public static Map<String, String> getCacheInstanceInfo() {
        return RedisUtil.mapGetAll(Constant.INST_WITH_MAP_KEY);
    }

    public static int singleIdMapTag(Long id) {
        // 例如实例3个，用户id: 1%3 = 1, 2%3=2, 3%3=0 => 3,tag有三个1,2,3
        Map<String, String> instanceWithTag = getCacheInstanceInfo();
        int mod = id.intValue() % instanceWithTag.size();
        return  mod == 0 ? instanceWithTag.size() : mod;
    }

    public static Map<Integer, List<Long>> multiGroupByTag(List<Long> ids) {
        Map<String, String> instanceWithTag = getCacheInstanceInfo();
        return ids.stream().collect(Collectors.groupingBy(e -> {
            int mod = e.intValue() % instanceWithTag.size();
            return mod == 0 ? instanceWithTag.size() : mod;
        }));
    }
}
