package com.hch.chat_simple.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hch.chat_simple.config.MinioConfig;
import com.hch.chat_simple.util.MinIOUtil;
import com.hch.chat_simple.util.Payload;
import com.hch.chat_simple.util.StatusCodeEnum;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/file/")
@AllArgsConstructor
public class FileUploadController {
    
    private MinioConfig minioConfig;
    private MinIOUtil minIOUtil;
    
    @GetMapping("/bucketExists")
    public Payload bucketExists(@RequestParam String bucketName) {
        return Payload.success(minIOUtil.bucketExists(bucketName));
    }

    @GetMapping("/makeBucket")
    public Payload makeBucket(@RequestParam String bucketName) {
        return Payload.success(minIOUtil.makeBucket(bucketName));
    }

    @GetMapping("/removeBucket")
    public Payload removeBucket(@RequestParam String bucketName) {
        return Payload.success(minIOUtil.removeBucket(bucketName));
    }

    @GetMapping("/getAllBuckets")
    public Payload getAllBuckets() {
        return Payload.success(minIOUtil.getAllBuckets());
    }

    @PostMapping("/upload")
    public Payload upload(@RequestParam("file") MultipartFile file) {
        String objectName = minIOUtil.upload(file);
        if (objectName != null) {
            return Payload.success(minioConfig.getViewUrl() + "/" + minioConfig.getBucketName() + "/" + objectName);
        }
        return Payload.of("上传失败", StatusCodeEnum.FILE_UPLOAD_FAIL);
    }

    @GetMapping("/preview")
    public Payload preview(@RequestParam String fileName) {
        return Payload.success(minIOUtil.preview(fileName));
    }

    @GetMapping("/download")
    public void download(@RequestParam String fileName, HttpServletResponse response) {
        minIOUtil.download(fileName, response);
        // return Payload.success("下载成功");
    }

    @GetMapping("/remove")
    public Payload remove(@RequestBody List<String> fileName) {
        for (String url : fileName) {
            String objectName = url.substring(url.lastIndexOf(minioConfig.getBucketName() + "/") + minioConfig.getBucketName().length() + 1);
            minIOUtil.remove(objectName);
        }
        return Payload.success("删除成功");
    }


    
} 
