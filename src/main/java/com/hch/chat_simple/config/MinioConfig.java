package com.hch.chat_simple.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    
    private String url;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    /**
     * 用于访问的外部暴露url
     */
    private String viewUrl;

    @Bean
    public MinioClient getMinioClient() {
        return MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
    }
}
