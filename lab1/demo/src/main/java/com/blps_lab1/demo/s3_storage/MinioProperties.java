package com.blps_lab1.demo.s3_storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
    String endpoint,
    String accessKey,
    String secretKey,
    String bucket
    ) {}
