package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.*;
import com.blps_lab1.demo.services.ReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private final IReservationService reservationService;
    private final IReservationDraftService reservationDraftService;

    public ReservationController(IReservationService reservationService, IReservationDraftService reservationDraftService) {
        this.reservationService = reservationService;
        this.reservationDraftService = reservationDraftService;
    }

    @PostMapping("/entity")
    public ResponseEntity<ReservationDto> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationDto response = reservationDraftService.createDraft(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/dates")
    public ResponseEntity<ReservationDto> updateDate(
            @PathVariable("id") Long id,
            @Valid @RequestBody DateRequest request
    ) {
        ReservationDto response = reservationDraftService.updateDate(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getById(@PathVariable("id") Long id) {
        ReservationDto response = reservationService.findReservation(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/cover-letter")
    public ResponseEntity<ReservationDto> updateCoverLetterByAdmin(
            @PathVariable("id") Long id,
            @RequestBody String newLetter
    ) {
        if (newLetter == null || newLetter.trim().isBlank()) {
            throw new IllegalArgumentException("Cover letter text cannot be null or empty");
        }
        return ResponseEntity.ok(reservationService.updateCoverLetterByAdmin(id, newLetter));
    }

    @PatchMapping("/drafts/{id}/moderate")
    public ResponseEntity<Void> moderateDraft(
            @PathVariable("id") Long id,
            @RequestParam("approved") boolean approved
    ) {
        if (reservationDraftService instanceof ReservationDraftService) {
            ((ReservationDraftService) reservationDraftService).moderateByAdmin(id, approved);
        }
        return ResponseEntity.ok().build();
    }
}