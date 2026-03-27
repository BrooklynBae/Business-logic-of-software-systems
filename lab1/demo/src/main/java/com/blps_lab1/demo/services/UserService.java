package com.blps_lab1.demo.services;

import com.blps_lab1.demo.dto.CreateUserRequest;
import com.blps_lab1.demo.dto.UserDto;
import com.blps_lab1.demo.data.repository.UserRepository;
import com.blps_lab1.demo.data.tables.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto updatePhoto(Long id, String photo) {
        userRepository.getReferenceById(id).setPhoto(photo);
        return new UserDto(id, userRepository.getReferenceById(id).getName(), photo);
    }

    public UserDto findById(Long id) {
        User user = userRepository.getReferenceById(id);

        return new UserDto(id, user.getName(), user.getPhoto());
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id = " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDto create(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setPhoto(request.getPhoto());

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getPhoto());
    }
}
