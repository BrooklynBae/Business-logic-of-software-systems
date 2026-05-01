package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.dto.CreateUserRequest;
import com.blps_lab1.demo.dto.UserDto;

public interface IUserService {
    UserDto updatePhoto(Long id, String photo);
    UserDto findById(Long id);
    void delete(Long id);
    UserDto create(CreateUserRequest request);
    User findEntityById(Long id);
}
