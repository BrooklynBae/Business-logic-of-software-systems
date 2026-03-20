package controller;

import dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.PaymentService;
import services.ReservationService;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }
    //сначала create draft -> payment = null, потом confirm payment ->
    //я возвращаю айди созданного черновика and price
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationRequest request) {
        ReservationDto response = reservationService.createDraft(request);
        return ResponseEntity.ok(response);
    }
    //id here - id of created draft reservation. обновить или задать в первый раз
    @PatchMapping("/{id}/payment")
    public ResponseEntity<PaymentResponseDto> confirmPayment(
            @PathVariable Long id,
            @RequestBody PaymentRequest request
    ) {
        PaymentResponseDto response = paymentService.confirmPayment(id, request);
        return ResponseEntity.ok(response);
    }
//айди draft
    @PatchMapping("/{id}/dates")
    public ResponseEntity<ReservationDto> updateDate(
            @PathVariable Long id,
            @RequestBody DateRequest request
    ) {
        ReservationDto response = reservationService.updateDate(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable Long id) {
        ReservationDto response = reservationService.findReservation(id);
        return ResponseEntity.ok(response);
    }
    //maybe useless
    @PostMapping("/{id}/payment/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@PathVariable Long id) {
        PaymentResponseDto response = reservationService.processPayment(id);
        return ResponseEntity.ok(response);
    }
}
