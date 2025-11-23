package org.mobilehub.payment_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        log.warn("[404] {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "timestamp", Instant.now().toString(),
                        "path", req.getRequestURI()
                ));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflict(ConflictException ex, HttpServletRequest req) {
        log.warn("[409] {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "timestamp", Instant.now().toString(),
                        "path", req.getRequestURI()
                ));
    }

    @ExceptionHandler(InvalidSignatureException.class)
    public ResponseEntity<?> handleInvalidSignature(InvalidSignatureException ex, HttpServletRequest req) {
        log.warn("[401] {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "timestamp", Instant.now().toString(),
                        "path", req.getRequestURI()
                ));
    }

    // PayOS prod-only: capture/refund không hỗ trợ
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<?> handleUnsupported(UnsupportedOperationException ex, HttpServletRequest req) {
        log.warn("[501] {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "timestamp", Instant.now().toString(),
                        "path", req.getRequestURI()
                ));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<?> handleBadRequest(RuntimeException ex, HttpServletRequest req) {
        log.warn("[400] {} {} -> {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of(
                        "error", ex.getMessage(),
                        "timestamp", Instant.now().toString(),
                        "path", req.getRequestURI()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fields.put(err.getField(), err.getDefaultMessage()));

        log.warn("[400][Validation] {} {} -> {}", req.getMethod(), req.getRequestURI(), fields);

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "error", "Validation failed",
                        "fields", fields,
                        "timestamp", Instant.now().toString(),
                        "path", req.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleOther(Exception ex, HttpServletRequest req) {
        // tạo errorId để trace nhanh
        String errorId = UUID.randomUUID().toString();

        // LOG FULL STACKTRACE
        log.error("[500][{}] {} {} -> {}", errorId, req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);

        // Trả về FE errorId + message (dev-friendly).
        // Nếu sợ lộ message prod thì bỏ "detail" đi.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal error",
                        "errorId", errorId,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now().toString(),
                        "path", req.getRequestURI()
                ));
    }
}
