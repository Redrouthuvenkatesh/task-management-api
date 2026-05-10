package com.taskmanager.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response payload.
 * <p>
 * errors field is populated for validation failures (400) only;
 * it is omitted (null → excluded via @JsonInclude) for other error types.
 */
@Value
@Builder
public class ApiError {

    int status;
    String error;
    String message;
    String path;
    List<String> errors;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();
}
