package controller;

import data.tables.User;
import dto.ReservationDto;
import dto.UpdatePaymentRequest;
import dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    }

    @PatchMapping("/{id}/photo")
    public ResponseEntity<UserDto> updatePhoto(
            @PathVariable Long id,
            @RequestBody String photo
    ) {
        UserDto response = UserService.updatePhoto(id, photo);
        return ResponseEntity.ok(response);
    }
}
