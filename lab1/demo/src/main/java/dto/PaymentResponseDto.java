package dto;

public class PaymentResponseDto {
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

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
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
}