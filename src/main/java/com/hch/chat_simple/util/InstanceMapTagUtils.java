package com.hch.chat_simple.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class InstanceMapTagUtils {
    
    public static Map<String, String> getCacheInstanceInfo() {
        return RedisUtil.mapGetAll(Constant.INST_WITH_MAP_KEY);
    }

    public static int singleIdMapTag(Long id) {
        Map<String, String> instanceWithTag = getCacheInstanceInfo();
        return id.intValue() % instanceWithTag.size() + 1;
    }

    public static Map<Integer, List<Long>> multiGroupByTag(List<Long> ids) {
        Map<String, String> instanceWithTag = getCacheInstanceInfo();
        return ids.stream().collect(Collectors.groupingBy(e -> e.intValue() % instanceWithTag.size() + 1));
    }
}
