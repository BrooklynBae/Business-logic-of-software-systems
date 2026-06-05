package com.blps_lab1.demo.services;

import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.dto.CreateUserRequest;
import com.blps_lab1.demo.dto.UserDto;
import com.blps_lab1.demo.data.repository.UserRepository;
import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.services.api.IMinioStorageService;
import com.blps_lab1.demo.services.api.IUserService;
import com.blps_lab1.demo.security.XmlUserRegistry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final IMinioStorageService minioStorageService;
    private final XmlUserRegistry xmlUserRegistry;

    public UserService(UserRepository userRepository, IMinioStorageService minioStorageService, XmlUserRegistry xmlUserRegistry) {
        this.userRepository = userRepository;
        this.minioStorageService = minioStorageService;
        this.xmlUserRegistry = xmlUserRegistry;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or @appSecurity.isSelfUser(#a0, authentication.name)")
    public UserDto updatePhoto(Long id, MultipartFile photoFile) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (photoFile == null || photoFile.isEmpty()) {
            throw new IllegalArgumentException("Photo file cannot be null or empty");
        }

        User user = findEntityById(id);

        String newObjectName = minioStorageService.uploadPhoto(photoFile);
        String oldObjectName = user.getPhoto();

        user.setPhoto(newObjectName);

        User saved = userRepository.save(user);

        if (oldObjectName != null) {
            minioStorageService.deletePhoto(oldObjectName);
        }

        return toDto(saved);
    }

    @Override
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or @userRepository.findById(#a0).orElse(null)?.getLogin() == authentication.name")
    public UserDto findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return toDto(findEntityById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or @userRepository.findById(#a0).orElse(null)?.getLogin() == authentication.name")
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
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
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhoto(request.getPhoto());
        user.setLogin(request.getLogin());

        User saved = userRepository.save(user);

        xmlUserRegistry.registerUserInXml(
                request.getLogin(),
                request.getPassword(),
                request.getRoles(),
                request.getAuthorities()
        );

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
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));
    }
}