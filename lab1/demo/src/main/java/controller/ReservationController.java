package controller;

import dto.CreateReservationRequest;
import dto.PlaceDto;
import dto.ReservationDto;
import dto.UpdatePaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.ReservationService;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody CreateReservationRequest request) {
        ReservationDto response = reservationService.createReservation(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<ReservationDto> updatePayment(
            @PathVariable Long id,
            @RequestBody UpdatePaymentRequest request
    ) {
        ReservationDto response = reservationService.updatePayment(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/dates")
    public ResponseEntity<ReservationDto> updatePayment(
            @PathVariable Long id,
            @RequestBody UpdatePaymentRequest request
    ) {
        ReservationDto response = reservationService.updateDate(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        ReservationDto response = reservationService.findReservation(id);
        return ResponseEntity.ok(response);
    }


}
