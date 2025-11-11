package org.mobilehub.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int error;        // 0 = success
    private String message;   // "success" or error reason
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "success", data);
    }
    public static <T> ApiResponse<T> fail(String msg) {
        return new ApiResponse<>(1, msg, null);
    }
}
