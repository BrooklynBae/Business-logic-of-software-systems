package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.PaymentRequest;
import com.blps_lab1.demo.dto.PaymentResponseDto;
import com.blps_lab1.demo.dto.ReservationDto;
import com.blps_lab1.demo.services.api.IPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final IPaymentService paymentService;

    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

//    @PatchMapping("/reservation/{id}")
//    public ResponseEntity<ReservationDto> updatePaymentData(@PathVariable Long id, @RequestBody PaymentRequest request) {
//        return ResponseEntity.ok(paymentService.updatePaymentData(id, request));
//    }

    @PostMapping("/reservation/{id}/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(id, request));
    }
}
