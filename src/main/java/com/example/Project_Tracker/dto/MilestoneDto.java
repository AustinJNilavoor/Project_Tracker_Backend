package com.example.Project_Tracker.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

import com.example.Project_Tracker.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a Milestone.")
public class MilestoneDto {

    @Schema(description = "Unique ID of the Milestone (Read-only on create)", example = "m-5678")
    private String id; 

    @NotBlank(message = "Milestone name is required")
    @Size(min = 3, max = 100, message = "Milestone name must be between 3 and 100 characters")
    @Schema(description = "Name of the milestone", example = "Backend API Finalized")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Detailed description of the milestone")
    private String description;

    @NotNull(message = "Status is required (Pending, In_Progress, Completed)")
    @Schema(implementation = Status.class, description = "Current status of the milestone")
    private Status status; 

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    @Schema(description = "Planned start date", example = "2024-01-15")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Schema(description = "Planned end date", example = "2024-03-30")
    private LocalDate endDate;

    @NotBlank(message = "Project ID is required")
    @Schema(description = "The ID of the parent project", example = "p-1234")
    private String projectId;

    @Schema(description = "Name of the parent project (Read-only convenience)")
    private String projectName; 

    @Schema(description = "ID of a milestone this milestone depends on (Optional)")
    private String dependsOnId;   
    
    @Schema(description = "Name of the dependency milestone (Read-only convenience)")
    private String dependsOnName; 
}