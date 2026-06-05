package com.blps_lab1.demo.dto;

import com.blps_lab1.demo.data.tables.PaymentMethod;
import com.blps_lab1.demo.data.tables.PaymentType;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull(message = "Payment type cannot be null")
    private PaymentType paymentType;

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    public PaymentRequest() {
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
