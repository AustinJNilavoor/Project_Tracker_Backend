package com.example.Project_Tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Response object containing JWT token after successful login.")
public class AuthResponse {

    @Schema(description = "JWT Bearer token for authentication")
    private String token;

    @Schema(description = "User's global role", example = "MANAGER", allowableValues = { "ADMIN", "MANAGER",
            "EMPLOYEE" })
    private String role;

    @Schema(description = "User's name", example = "Alice Smith")
    private String name;

    @Schema(description = "User's id", example = "12345")
    private String id;
}