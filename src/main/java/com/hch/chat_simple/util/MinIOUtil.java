package com.hch.chat_simple.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.hch.chat_simple.config.MinioConfig;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jodd.io.FastByteArrayOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MinIOUtil {
    
    @Resource
    private MinioConfig minioConfig;
    @Resource
    private MinioClient minioClient;


    public Boolean bucketExists(String bucketName) {
        Boolean found;

        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("bucketExists exception: ", e);
            return false;
        }

        return found;
    }

    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("makeBucket exception", e);
            return false;
        } 
        return true;
    }

    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().build());
        } catch (Exception e) {
            log.error("removeBucket exception: ", e);
            return false;
        } 
        return true;
    }

    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("getAllBuckets exception: ", e);
        } 
        return null;
    }

    public String upload(MultipartFile file) {
        String originFileName = file.getOriginalFilename();
        log.info("upload originFileName: {}", originFileName);

        if (!StringUtils.isNotBlank(originFileName)) {
            throw new RuntimeException("上传文件名为空");
        }

        String fileName = UUID.randomUUID() + originFileName.substring(originFileName.lastIndexOf("."));
        String date = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDate.now());
        String objectName = date + "/" + fileName;

        PutObjectArgs objectArgs;
        try {
            objectArgs = PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType()).build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            log.error("upload exception", e);
            return null;
        } 
        return objectName;
        
    }

    public String preview(String fileName) {
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs
            .builder()
            .bucket(minioConfig.getBucketName())
            .object(fileName)
            .method(Method.GET)
            .build();
        String url;
        try {
            url = minioClient.getPresignedObjectUrl(build);
            return url;
        } catch (Exception e) {
            log.error("preview exception: ", e);
        } 
        return null;
    }

    public void download(String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(minioConfig.getBucketName())
            .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            log.error("download exception: ", e);
        }
    }

    public List<Item> listObjects() {
        Iterable<Result<Item>> results = minioClient.listObjects(
            ListObjectsArgs.builder().bucket(minioConfig.getBucketName()).build()
        );
        List<Item> items = new ArrayList<>();

        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            log.error("object get exception ", e);
            return null;
        } 
        return items;
    }

    public boolean remove(String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(minioConfig.getBucketName())
                .object(fileName)
                .build()
            );
        } catch (Exception e) {
            log.error("remove exception: ", e);
            return false;
        } 
        return true;
    }
    

}
