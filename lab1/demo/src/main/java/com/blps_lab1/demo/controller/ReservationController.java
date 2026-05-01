package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.*;
import com.blps_lab1.demo.services.api.IReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final IReservationService reservationService;

    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }
    //сначала create draft -> payment = null, потом confirm payment ->
    //я возвращаю айди созданного черновика and price
    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationRequest request) {
        ReservationDto response = reservationService.createDraft(request);
        return ResponseEntity.ok(response);
    }

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
