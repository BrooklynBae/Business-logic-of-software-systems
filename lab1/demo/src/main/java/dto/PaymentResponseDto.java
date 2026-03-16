package dto;

public class PaymentResponseDto {
    private Long draftResId;
    private boolean available;
    private boolean success;
    private String message;

    public PaymentResponseDto() {
    }

    public PaymentResponseDto(Long draftResId, boolean available, boolean success, String message) {
        this.draftResId = draftResId;
        this.available = available;
        this.success = success;
        this.message = message;
    }

    public Long getDraftResId() {
        return draftResId;
    }

    public void setDraftResId(Long userId) {
        this.draftResId = userId;
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