package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.ApiErrorDto;
import com.blps_lab1.demo.exception.ApiException;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorDto> handleApiException(ApiException exception) {
        return buildResponse(exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleNotFoundException(NotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDto> handleBadRequestException(BadRequestException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalState(IllegalStateException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponse("Validation failed: " + errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getRootCause();
        if (rootCause != null && rootCause.getMessage() != null) {
            String rawMsg = rootCause.getMessage();
            if (rawMsg.contains("not one of the values accepted for Enum class")) {
                int startBracket = rawMsg.indexOf("[");
                int endBracket = rawMsg.indexOf("]");
                if (startBracket != -1 && endBracket != -1) {
                    String allowedValues = rawMsg.substring(startBracket, endBracket + 1);
                    return buildResponse("Invalid value provided. Accepted values for this field are: " + allowedValues, HttpStatus.BAD_REQUEST);
                }
            }
        }
        String detailedMessage = (rootCause != null) ? rootCause.getMessage() : ex.getMessage();
        return buildResponse("Malformed JSON request or invalid values: " + detailedMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorDto> handleDataIntegrity(DataIntegrityViolationException ex) {
        return buildResponse("Database integrity constraint violation. Check your input data.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorDto> handleAuthenticationException(AuthenticationException ex) {
        return buildResponse("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AccessDeniedException> handleAccessDenied(AccessDeniedException ex) {
        throw ex;
    }

    @ExceptionHandler(com.blps_lab1.demo.exception.JwtAuthenticationException.class)
    public ResponseEntity<ApiErrorDto> handleJwtException(com.blps_lab1.demo.exception.JwtAuthenticationException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleUnhandled(Exception exception) {
        exception.printStackTrace();
        return buildResponse("An unexpected server error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiErrorDto> buildResponse(String message, HttpStatus status) {
        ApiErrorDto error = ApiErrorDto.builder()
                .message(message)
                .status(status.value())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}