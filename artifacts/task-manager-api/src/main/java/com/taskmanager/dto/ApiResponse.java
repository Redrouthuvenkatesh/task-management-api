package com.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Generic API response envelope for consistent JSON structure across all endpoints.
 * <p>
 * Design decision: wrapping responses in a typed envelope lets clients rely on
 * predictable fields (success, message, data, timestamp) regardless of resource type.
 */
@Value
@Builder
public class ApiResponse<T> {

    boolean success;
    String message;
    T data;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }
}
