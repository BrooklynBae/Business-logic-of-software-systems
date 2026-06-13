package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.*;
import com.blps_lab1.demo.services.api.IReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.InputStream;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final IReservationService reservationService;
    private final IReservationDraftService reservationDraftService;
    private final Repository jackrabbitRepository;

    public ReservationController(IReservationService reservationService,
                                 IReservationDraftService reservationDraftService,
                                 Repository jackrabbitRepository) {
        this.reservationService = reservationService;
        this.reservationDraftService = reservationDraftService;
        this.jackrabbitRepository = jackrabbitRepository;
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
        reservationDraftService.moderateByAdmin(id, approved);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/drafts/{id}/owner-confirm")
    public ResponseEntity<Void> ownerConfirmDraft(
            @PathVariable("id") Long id,
            @RequestParam("approved") boolean approved
    ) {
        reservationDraftService.confirmByOwner(id, approved);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/contracts/{reservationId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getContractPdfFromKis(@PathVariable("reservationId") Long reservationId) {
        Session jcrSession = null;
        try {
            jcrSession = jackrabbitRepository.login(new SimpleCredentials("admin", "admin".toCharArray()));
            Node rootNode = jcrSession.getRootNode();

            String jcrFilePath = "corporate_contracts/contract_legal_" + reservationId + ".pdf/jcr:content";

            if (!rootNode.hasNode(jcrFilePath)) {
                return ResponseEntity.notFound().build();
            }

            Node contentNode = rootNode.getNode(jcrFilePath);

            Binary jcrBinary = contentNode.getProperty("jcr:data").getBinary();
            long size = jcrBinary.getSize();
            byte[] pdfBytes = new byte[(int) size];

            try (InputStream inputStream = jcrBinary.getStream()) {
                inputStream.readNBytes(pdfBytes, 0, (int) size);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Content-Disposition", "inline; filename=contract_" + reservationId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            throw new RuntimeException("Критическая ошибка чтения бинарного потока из КИС", e);
        } finally {
            if (jcrSession != null && jcrSession.isLive()) {
                jcrSession.logout();
            }
        }
    }
}