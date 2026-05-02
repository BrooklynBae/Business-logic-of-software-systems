package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.tables.PaymentType;
import com.blps_lab1.demo.data.tables.User;
import com.blps_lab1.demo.dto.PaymentRequest;
import com.blps_lab1.demo.dto.PaymentResponseDto;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.IPaymentService;
import com.blps_lab1.demo.services.api.IReservationService;
import com.blps_lab1.demo.services.utils.ReservationDraftStorage;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PaymentService implements IPaymentService {

    private final ReservationDraftStorage draftStorage;
    private final IReservationService reservationService;
    private final Random random = new Random();

    public PaymentService(ReservationDraftStorage draftStorage, IReservationService reservationService) {
        this.draftStorage = draftStorage;
        this.reservationService = reservationService;
    }

    @Override
    public ReservationDto updatePaymentData(Long id, PaymentRequest request) {
        ReservationDto reservationDto = draftStorage.getDraft(id);
        if (reservationDto == null) {
            throw new NotFoundException("Draft not found or expired");
        }
        if (request.getPaymentMethod() == null || request.getPaymentType() == null) {
            throw new BadRequestException("Payment type and payment method are required");
        }

        reservationDto.setPaymentMethod(request.getPaymentMethod());
        reservationDto.setPaymentType(request.getPaymentType());

        return reservationDto;
    }

    @Override
    public PaymentResponseDto processPayment(Long id) {
        ReservationDto reservationDto = draftStorage.getDraft(id);
        if (reservationDto == null) {
            throw new NotFoundException("Draft not found or expired");
        }

        User user = reservationDto.getUser();

        if (user.getPhoto() == null || user.getPhoto().isBlank()) {
            return PaymentResponseDto.builder()
                    .reservationId(id)
                    .available(false)
                    .success(false)
                    .message("Please, add your photo")
                    .build();
        }
        if (reservationDto.getPaymentType() == null || reservationDto.getPaymentMethod() == null) {
            throw new BadRequestException("Payment data must be set before processing payment");
        }

        if (reservationDto.getPaymentType().equals(PaymentType.NOW)) {
            if (random.nextBoolean()) {
                Long reservationId = reservationService.confirmReservation(id);
                draftStorage.removeDraft(id);
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
            Long reservationId = reservationService.confirmReservation(id);
            draftStorage.removeDraft(id);
            return PaymentResponseDto.builder()
                    .reservationId(reservationId)
                    .available(true)
                    .success(true)
                    .message("Reservation confirmed, payment deferred")
                    .build();
        }
    }
}
