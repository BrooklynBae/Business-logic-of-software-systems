package com.blps_lab1.demo.dto;

import java.time.Instant;

public class ApiErrorDto {
    public static Builder builder() {
        return new Builder();
    }

    private final String message;
    private final int status;
    private final Instant timestamp;

    public ApiErrorDto(String message, int status, Instant timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public static class Builder {
        private String message;
        private int status;
        private Instant timestamp;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiErrorDto build() {
            return new ApiErrorDto(message, status, timestamp);
        }
    }
}
