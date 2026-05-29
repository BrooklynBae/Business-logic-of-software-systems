package com.blps_lab1.demo.services;

import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.dto.CreateUserRequest;
import com.blps_lab1.demo.dto.UserDto;
import com.blps_lab1.demo.data.repository.UserRepository;
import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.services.api.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService implements IUserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto updatePhoto(Long id, String photo) {
        User user = findEntityById(id);
        user.setPhoto(photo);
        return toDto(user);
    }

    @Override
    public UserDto findById(Long id) {
        return toDto(findEntityById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        findEntityById(id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDto create(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setPhoto(request.getPhoto());

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .photo(user.getPhoto())
                .build();
    }

    @Override
    public User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));
    }

}
