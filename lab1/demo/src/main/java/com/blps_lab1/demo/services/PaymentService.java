package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.tables.*;
import com.blps_lab1.demo.dto.*;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService implements IPaymentService {

    private final IReservationDraftService reservationDraftService;
    private final IReservationService reservationService;

    public PaymentService(IReservationDraftService reservationDraftService, IReservationService reservationService) {
        this.reservationDraftService = reservationDraftService;
        this.reservationService = reservationService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_PROCESS_PAYMENT') and @appSecurity.isDraftOwner(#a0, authentication.name)")
    public PaymentResponseDto processPayment(Long id, PaymentRequest request) {
        validatePaymentAllowed(id, request);

        Long reservationId = reservationService.confirmReservation(id, request);
        reservationDraftService.removeDraft(id);

        return PaymentResponseDto.builder()
                .reservationId(reservationId)
                .available(true)
                .success(true)
                .message("Payment processed, reservation activated")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("permitAll()")
    public PaymentResponseDto preparePaymentFromProcess(Long id, PaymentRequest request) {
        validatePaymentAllowed(id, request);
        return PaymentResponseDto.builder()
                .reservationId(null)
                .available(true)
                .success(true)
                .message("Payment accepted by Camunda process; reservation will be finalized asynchronously")
                .build();
    }

    private void validatePaymentAllowed(Long id, PaymentRequest request) {
        if (id == null || request == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        ReservationDraft reservationDraft = reservationDraftService.findEntityById(id);
        if (reservationDraft == null) {
            throw new NotFoundException("Draft not found or expired");
        }

        User user = reservationDraft.getUser();
        if (user.getPhoto() == null || user.getPhoto().isBlank()) {
            throw new BadRequestException("Payment failed: Please, add your photo before processing payment");
        }

        Place place = reservationDraft.getPlace();
        if (place.getOwner() != null && Boolean.TRUE.equals(place.getOwner().getRequirenmentsMessage())) {
            String letter = reservationDraft.getCoverLetter();

            if (letter == null || letter.startsWith("[PENDING_ADMIN]")) {
                throw new BadRequestException("Payment failed: Your letter is pending admin moderation.");
            }
            if (letter.startsWith("[APPROVED_BY_ADMIN]")) {
                throw new BadRequestException("Payment failed: Administration approved, but the email is currently being sent to the owner.");
            }
            if (letter.startsWith("[EMAIL_SENT_TO_OWNER]")) {
                throw new BadRequestException("Payment failed: Owner has not confirmed the reservation yet.");
            }
            if (letter.startsWith("[REJECTED]")) {
                throw new BadRequestException("Payment failed: Your application was rejected.");
            }
            if (letter.startsWith("[REJECTED_BY_OWNER]")) {
                throw new BadRequestException("Payment failed: Owner rejected the reservation.");
            }
            if (!letter.startsWith("[APPROVED_BY_OWNER]")) {
                throw new BadRequestException("Payment failed: Reservation is not approved by owner.");
            }
        }

        if (request.getPaymentType() == null || request.getPaymentMethod() == null) {
            throw new BadRequestException("Payment data must be set before processing payment");
        }
    }
}
