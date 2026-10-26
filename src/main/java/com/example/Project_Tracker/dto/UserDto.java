package com.example.Project_Tracker.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a User.")
public class UserDto {
    
    @Schema(description = "Unique ID of the User (Read-only)", example = "u-9876")
    private String id;
    
    @Schema(description = "Full name of the user", example = "Alice Smith")
    private String name;
    
    @Schema(description = "User's unique email address", example = "alice.smith@example.com")
    private String email;
    
    @Schema(description = "User's global role", example = "MANAGER", allowableValues = {"ADMIN", "MANAGER", "EMPLOYEE"})
    private String role;

    // 🔹 Flattened IBU relation
    @Schema(description = "ID of the IBU the user belongs to (Optional)")
    private String ibuId;
    
    @Schema(description = "Name of the IBU the user belongs to (Read-only convenience)")
    private String ibuName;
}