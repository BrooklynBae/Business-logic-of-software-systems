package dto;

import data.tables.PaymentMethod;
import data.tables.PaymentType;

public class PaymentRequest {
    private PaymentType paymentType;
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