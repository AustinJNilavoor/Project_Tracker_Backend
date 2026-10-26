package com.example.Project_Tracker.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

import com.example.Project_Tracker.enums.Priority;
import com.example.Project_Tracker.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for a Project.")
public class ProjectDto {

    @Schema(description = "Unique ID of the Project (Read-only on create)", example = "p-1234")
    private String id; 

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    @Schema(description = "Name of the project", example = "Customer Portal Redesign")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Detailed description of the project")
    private String description;

    @NotNull(message = "Status is required (e.g., Pending, In Progress, Completed)")
    @Schema(implementation = Status.class, description = "Current status of the project")
    private Status status;

    @NotNull(message = "Priority is required (e.g., Low, Medium, High)")
    @Schema(implementation = Priority.class, description = "Priority of the project")
    private Priority priority;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    @Schema(description = "Start date of the project", example = "2024-01-01")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Expected end date of the project", example = "2024-06-30")
    private LocalDate endDate;
}