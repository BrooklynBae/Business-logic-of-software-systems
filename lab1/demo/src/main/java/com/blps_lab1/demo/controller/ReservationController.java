package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.*;
import com.blps_lab1.demo.services.PaymentService;
import com.blps_lab1.demo.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ReservationDto> updatePaymentData(
            @PathVariable Long id,
            @RequestBody PaymentRequest request
    ) {
        ReservationDto response = paymentService.updatePaymentData(id, request);
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

    @PostMapping("/{id}/payment/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@PathVariable Long id) {
        PaymentResponseDto response = paymentService.processPayment(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/entity")
    public ResponseEntity<ReservationDto> createReservationEntity(@RequestBody CreateReservationEntityRequest request) {
        ReservationDto response = reservationService.createReservationEntity(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
