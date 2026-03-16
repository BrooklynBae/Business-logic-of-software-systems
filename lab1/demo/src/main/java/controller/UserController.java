package controller;

import dto.ReservationDto;
import dto.UpdatePaymentRequest;
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
    public ResponseEntity<ReservationDto> updatePhoto(
            @PathVariable Long id,
            @RequestBody String photo
    ) {
        ReservationDto response = UserService.updateDate(id, request);
        return ResponseEntity.ok(response);
    }

}
