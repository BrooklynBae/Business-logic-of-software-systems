package com.blps_lab1.demo.services;

import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.dto.CreateUserRequest;
import com.blps_lab1.demo.dto.UserDto;
import com.blps_lab1.demo.data.repository.UserRepository;
import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.services.api.IMinioStorageService;
import com.blps_lab1.demo.services.api.IUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final IMinioStorageService minioStorageService;


    public UserService(UserRepository userRepository, IMinioStorageService minioStorageService) {
        this.userRepository = userRepository;
        this.minioStorageService = minioStorageService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or @appSecurity.isSelfUser(#a0, authentication.name)") //мб добавить валидацию фото
    public UserDto updatePhoto(Long id, MultipartFile photoFile) {
        User user = findEntityById(id);

        String newObjectName = minioStorageService.uploadPhoto(photoFile);
        String oldObjectName = user.getPhoto();

        user.setPhoto(newObjectName);

        User saved = userRepository.save(user);

        if (oldObjectName != null) {
            minioStorageService.deletePhoto(oldObjectName);
        }

        return toDto(user);
    }

    @Override
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or @userRepository.findById(#a0).orElse(null)?.getLogin() == authentication.name")
    public UserDto findById(Long id) {
        return toDto(findEntityById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or @userRepository.findById(#a0).orElse(null)?.getLogin() == authentication.name")
    public void delete(Long id) {
        User user = findEntityById(id);
        String photoName = user.getPhoto();

        userRepository.deleteById(id);

        if (photoName != null) {
            minioStorageService.deletePhoto(photoName);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("permitAll()")
    public UserDto create(CreateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setPhoto(request.getPhoto());
        user.setLogin(request.getLogin());

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
