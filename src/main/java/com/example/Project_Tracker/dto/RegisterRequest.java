package com.example.Project_Tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request object for a new user registration (Admin only).")
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name of the user", example = "Jane Doe")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User's unique email address", example = "jane.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    @Schema(description = "User's initial password")
    private String password;

    @NotBlank(message = "Role is required (e.g., ADMIN, MANAGER, EMPLOYEE)")
    @Schema(description = "User's global role", example = "EMPLOYEE", allowableValues = {"ADMIN", "MANAGER", "EMPLOYEE"})
    private String role;

    @Schema(description = "Name of the IBU the user belongs to (Optional for ADMIN/MANAGER, recommended for EMPLOYEE)")
    private String ibuName; // optional
}