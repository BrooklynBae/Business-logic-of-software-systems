package com.blps_lab1.demo.services.api;

import org.springframework.web.multipart.MultipartFile;

public interface IMinioStorageService {
    String uploadPhoto(MultipartFile file);
    void deletePhoto(String objectName);
}
