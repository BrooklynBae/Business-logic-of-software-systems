package controller;

import dto.ReservationDto;
import dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        ReservationDto response = reservationService.findReservation(id);
        return ResponseEntity.ok(response);
    } //find user or users res?

    @PatchMapping("/{id}/photo")
    public ResponseEntity<UserDto> updatePhoto(
            @PathVariable Long id,
            @RequestBody String photo
    ) {
        UserDto response = UserService.updatePhoto(id, photo);
        return ResponseEntity.ok(response);
    }
}
