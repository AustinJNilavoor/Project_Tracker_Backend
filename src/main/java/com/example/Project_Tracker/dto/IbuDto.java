package com.example.Project_Tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Data Transfer Object for an International Business Unit (IBU).")
public class IbuDto {

    @Schema(description = "Unique ID of the IBU (Read-only on create)", example = "f4882989-7366-49ea-84b1-a0514beeea59")
    private String id; 

    @NotBlank(message = "IBU name is required")
    @Size(min = 2, max = 100, message = "IBU name must be between 2 and 100 characters")
    @Schema(description = "Name of the IBU", example = "Enterprise Solutions")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Schema(description = "Description of the IBU's focus")
    private String description;
}