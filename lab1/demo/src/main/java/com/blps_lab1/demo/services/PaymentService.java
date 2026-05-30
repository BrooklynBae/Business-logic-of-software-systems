package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.tables.PaymentType;
import com.blps_lab1.demo.data.tables.ReservationDraft;
import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.dto.PaymentRequest;
import com.blps_lab1.demo.dto.PaymentResponseDto;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.IPaymentService;
import com.blps_lab1.demo.services.api.IReservationDraftService;
import com.blps_lab1.demo.services.api.IReservationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class PaymentService implements IPaymentService {

    private final IReservationDraftService reservationDraftService;
    private final IReservationService reservationService;
    private final Random random = new Random();

    public PaymentService(@Lazy IReservationDraftService reservationDraftService, @Lazy IReservationService reservationService) {
        this.reservationDraftService = reservationDraftService;
        this.reservationService = reservationService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_PROCESS_PAYMENT') and @appSecurity.isDraftOwner(#a0, authentication.name)")
    public PaymentResponseDto processPayment(Long id, PaymentRequest request) {

        ReservationDraft reservationDraft = reservationDraftService.findEntityById(id);
        if (reservationDraft == null) {
            throw new NotFoundException("Draft not found or expired");
        }

        User user = reservationDraft.getUser();
        if (user.getPhoto() == null || user.getPhoto().isBlank()) {
            return PaymentResponseDto.builder()
                    .reservationId(id)
                    .available(false)
                    .success(false)
                    .message("Please, add your photo")
                    .build();
        }
        if (request.getPaymentType() == null || request.getPaymentMethod() == null) {
            throw new BadRequestException("Payment data must be set before processing payment");
        }

        if (request.getPaymentType().equals(PaymentType.NOW)) {
            if (random.nextBoolean()) {
                Long reservationId = reservationService.confirmReservation(id, request);
                reservationDraftService.removeDraft(id);
                return PaymentResponseDto.builder()
                        .reservationId(reservationId)
                        .available(true)
                        .success(true)
                        .message("Payment processed")
                        .build();
            } else {
                return PaymentResponseDto.builder()
                        .reservationId(id)
                        .available(true)
                        .success(false)
                        .message("Payment failed")
                        .build();
            }
        } else {
            Long reservationId = reservationService.confirmReservation(id, request);
            reservationDraftService.removeDraft(id);
            return PaymentResponseDto.builder()
                    .reservationId(reservationId)
                    .available(true)
                    .success(true)
                    .message("Reservation confirmed, payment deferred")
                    .build();
        }
    }
}
