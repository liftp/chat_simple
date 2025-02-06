package com.hch.chat_simple.util;


import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class HttpUrlUtils {
    
    public static MultiValueMap<String, String> getUriParams(String uri) {

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri).build();
        return uriComponents.getQueryParams();
    }
}
