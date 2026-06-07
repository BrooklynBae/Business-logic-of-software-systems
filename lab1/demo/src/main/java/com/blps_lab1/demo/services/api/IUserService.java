package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.dto.CreateUserRequest;
import com.blps_lab1.demo.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    UserDto updatePhoto(Long id, MultipartFile photo);
    UserDto findById(Long id);
    void delete(Long id);
    UserDto create(CreateUserRequest request);
    User findEntityById(Long id);
}
