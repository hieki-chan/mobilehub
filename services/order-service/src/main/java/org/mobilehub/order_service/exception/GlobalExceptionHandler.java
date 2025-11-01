package org.mobilehub.order_service.exception;

import org.mobilehub.shared.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "org.mobilehub.order_service.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFound(OrderNotFoundException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }


    @ExceptionHandler(OrderCannotBeCancelledException.class)
    public ResponseEntity<?> handleCancelError(OrderCannotBeCancelledException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(400)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }


    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<?> handleInvalidStatus(InvalidOrderStatusException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(400)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .code(500)
                .message("Internal Server Error: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
