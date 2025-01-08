package com.hch.chat_simple.util;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageBean<T> {

    @Schema(description = "页数")
    private Long page;

    @Schema(description = "每页条数")
    private Long size;

    @Schema(description = "分页数据")
    private List<T> list;
    
}
