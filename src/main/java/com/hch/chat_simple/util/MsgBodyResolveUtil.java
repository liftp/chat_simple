package com.hch.chat_simple.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hch.chat_simple.exception.BusinessException;

public class MsgBodyResolveUtil {

    public static List<String> bodySplitByDelimiter(String msg, Integer size) {
        return bodySplitByDelimiter(msg, size, ",");
    }

    public static List<String> bodySplitByDelimiter(String msg, Integer size, String delimiter) {
        List<String> arr = new ArrayList<>();
        int index = 0;
        int lastIndex = 0;
        for (int i = 0; i < size; i++) {
            index = msg.indexOf(delimiter, lastIndex);
            if (index == -1) {
                throw new BusinessException("拆分msg格式不正确");
            }
            arr.add(msg.substring(lastIndex > 0 ? lastIndex + 1 : 0, index));
            lastIndex = index;
        }
        // 拆分后，末尾也需要取出来，数据区
        arr.add(msg.substring(index + 1));
        return arr;
    }

    /**
     * 检车列表字符串的正则匹配情况
     * @param data      数据集
     * @param regs      正则集
     * @return          -1：输入不满足，data.size() 则全部通过, 小于data.size() 则是不匹配数据的第一个下标
     */
    public static int checkListStringReg(List<String> data, List<String> regs) {
        if (CollectionUtils.isEmpty(data) || CollectionUtils.isEmpty(regs)) {
            return -1;
        }
        if (data.size() != regs.size()) {
            return -1;
        }
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).matches(regs.get(i))) {
                return i;
            }
        }
        return data.size();
    }

    /**
     * 使用逗号拆分并校验正则格式
     * @param msg       消息内容
     * @param regs      正则格式
     * @return
     */
    public static List<String> splitByCommaWith2Size(String msg, List<String> regs) {
        int startFlagSize = regs.size();
        List<String> data = bodySplitByDelimiter(msg, startFlagSize);
        if (CollectionUtils.isEmpty(data) || data.size() < (startFlagSize + 1)) {
            throw new BusinessException("拆分格式异常");
        }
        // 拆分后格式验证
        int result = checkListStringReg(data.subList(0, startFlagSize), regs);
        if (result < startFlagSize) {
            throw new BusinessException("拆分格式异常");
        }
        return data;
    }
}
