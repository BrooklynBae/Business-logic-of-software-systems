package com.blps_lab1.demo.dto;

public class PaymentResponseDto {
    public static Builder builder() {
        return new Builder();
    }

    private Long reservationId;
    private boolean available;
    private boolean success;
    private String message;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(Long reservationId, boolean available, boolean success, String message) {
        this.reservationId = reservationId;
        this.available = available;
        this.success = success;
        this.message = message;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long userId) {
        this.reservationId = userId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Builder {
        private Long reservationId;
        private boolean available;
        private boolean success;
        private String message;

        public Builder reservationId(Long reservationId) {
            this.reservationId = reservationId;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public PaymentResponseDto build() {
            return new PaymentResponseDto(reservationId, available, success, message);
        }
    }
}