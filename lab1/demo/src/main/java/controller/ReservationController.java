package controller;

import dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody UpdateDateRequest request
    ) {
        ReservationDto response = reservationService.updateDate(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        ReservationDto response = reservationService.findReservation(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/payment/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@PathVariable Long id) {
        PaymentResponseDto response = reservationService.processPayment(id);
        return ResponseEntity.ok(response);
    }
}
