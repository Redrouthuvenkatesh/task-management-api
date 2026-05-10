package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for login credential submission.
 */
@Value
@Builder
@Jacksonized
public class AuthRequest {

    @NotBlank(message = "Username must not be blank")
    String username;

    @NotBlank(message = "Password must not be blank")
    String password;
}
