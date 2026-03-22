package services;

import data.repository.UserRepository;
import data.tables.User;
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

    public UserDto findById(Long id) {
        User user = userRepository.getReferenceById(id);

        return new UserDto(id, user.getName(), user.getPhoto());
    }
}
