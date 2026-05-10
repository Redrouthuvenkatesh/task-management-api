package com.taskmanager.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO wrapping the JWT token returned after successful authentication.
 */
@Value
@Builder
public class AuthResponse {

    String token;
    String type;
    String username;

    public static AuthResponse of(String token, String username) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .username(username)
                .build();
    }
}
