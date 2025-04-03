package com.hch.chat_simple.enums;

import lombok.Getter;

@Getter
public enum ApplyStatusEnum {
    APPLYING(0, "申请中"),
    APPLY_PASS(1, "申请通过"),
    APPLY_REJECT(2, "申拒绝");
    private Integer status;
    private String desc;
    ApplyStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getDescByStatus(Integer status) {
        if (status == null) {
            return "";
        }
        for (ApplyStatusEnum e : ApplyStatusEnum.values()) {
            if (e.getStatus().equals(status)) {
                return e.getDesc();
            }
        }
        return "";

    }
}
