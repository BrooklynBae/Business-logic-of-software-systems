package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.ApiErrorDto;
import com.blps_lab1.demo.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorDto> handleApiException(ApiException exception) {
        ApiErrorDto error = ApiErrorDto.builder()
                .message(exception.getMessage())
                .status(exception.getStatus().value())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(exception.getStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleUnhandled(Exception exception) {
        ApiErrorDto error = ApiErrorDto.builder()
                .message(exception.getMessage() != null ? exception.getMessage() : "Unexpected server error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
