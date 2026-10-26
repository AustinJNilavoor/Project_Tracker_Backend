package com.example.Project_Tracker.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

import com.example.Project_Tracker.enums.Priority;
import com.example.Project_Tracker.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a Task.")
public class TaskDto {

    @Schema(description = "Unique ID of the Task (Read-only on create)", example = "t-9012")
    private String id; 

    @NotBlank(message = "Task title is required")
    @Schema(description = "Title of the task", example = "Implement user authentication endpoint")
    private String title;

    // @NotBlank(message = "Task description is required")
    @Schema(description = "Detailed description of the task")
    private String description;

    @NotNull(message = "Task status is required")
    @Schema(implementation = TaskStatus.class, description = "Current status of the task")
    private TaskStatus status;

    @NotNull(message = "Task priority is required")
    @Schema(implementation = Priority.class, description = "Priority of the task")
    private Priority priority;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date cannot be in the past")
    @Schema(description = "The target completion date", example = "2024-03-01")
    private LocalDate dueDate;

    // 🔹 Relations (flattened for frontend convenience)
    @NotBlank(message = "Project ID is required")
    @Schema(description = "The ID of the parent project", example = "p-1234")
    private String projectId;

    @Schema(description = "Name of the parent project (Read-only convenience)")
    private String projectName; 

    @Schema(description = "ID of the user assigned to this task (Optional)")
    private String assignedToId;
    
    @Schema(description = "Name of the assigned user (Read-only convenience)")
    private String assignedToName;

    @NotBlank(message = "Milestone ID is required")
    @Schema(description = "The ID of the parent milestone", example = "m-5678")
    private String milestoneID;
}