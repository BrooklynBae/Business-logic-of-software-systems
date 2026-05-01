package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.dto.PaymentRequest;
import com.blps_lab1.demo.dto.PaymentResponseDto;
import com.blps_lab1.demo.dto.ReservationDto;

public interface IPaymentService {
    ReservationDto updatePaymentData(Long id, PaymentRequest request);
    PaymentResponseDto processPayment(Long id);
}
