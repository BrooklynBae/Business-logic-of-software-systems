package services;

import data.repository.UserRepository;
import dto.UserDto;
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
}
