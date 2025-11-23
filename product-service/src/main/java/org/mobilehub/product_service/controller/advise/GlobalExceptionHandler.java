package org.mobilehub.product_service.controller.advise;

import org.mobilehub.product_service.exception.ProductNotFoundException;
import org.mobilehub.product_service.exception.VariantNotFoundException;
import org.mobilehub.shared.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "org.mobilehub.product.controller")
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?> handleProductNotFoundException(ProductNotFoundException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder().code(400).message(ex.getMessage()).build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(VariantNotFoundException.class)
    public ResponseEntity<?> handleVariantNotFoundException(VariantNotFoundException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder().code(400).message(ex.getMessage()).build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder().code(500).message(ex.getMessage()).build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiResponse);
    }
}
