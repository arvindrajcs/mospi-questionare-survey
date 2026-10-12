package com.mospi.questionare_survey.exception;

import com.mospi.questionare_survey.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SDDException.class)
    public ResponseEntity<Map<String, Object>> handleSDDException(SDDException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", ex.isStatus());
        body.put("message", SafeErrorMessage.from(ex));

        if (ex.getResponse() != null && ex.getResponse().containsKey("errorCode")) {
            body.put("errorCode", ex.getResponse().get("errorCode"));
        } else if (ex.getResponse() != null && !ex.getResponse().isEmpty()) {
            body.put("response", ex.getResponse());
        }

        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    // Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        ApiResponse<Object> response = new ApiResponse<>(
                null,
                false,
                SafeErrorMessage.from(ex)
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    // Invalid JSON / Request Body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "Invalid request body"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // General / Unexpected Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(
            Exception ex) {

        // Log the exception internally using your configured logger.
        // Do not expose internal exception details to API clients.

        ApiResponse<Object> response = new ApiResponse<>(
                null,
                false,
                "Something went wrong. Please try again later."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}