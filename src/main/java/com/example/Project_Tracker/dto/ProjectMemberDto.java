package com.example.Project_Tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import com.example.Project_Tracker.enums.ProjectRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a Project Member.")
public class ProjectMemberDto {

    @NotBlank(message = "Project ID is required")
    @Schema(description = "ID of the project the member belongs to", example = "p-1234")
    private String projectId;

    @Schema(description = "Name of the project (Read-only convenience)")
    private String projectName;

    @NotBlank(message = "User ID is required")
    @Schema(description = "ID of the user being added/managed", example = "u-9876")
    private String userId;

    @Schema(description = "Name of the user (Read-only convenience)")
    private String userName;

    @NotNull(message = "Role is required (e.g., PROJECT_MANAGER, DEVELOPER, TESTER)")
    @Schema(implementation = ProjectRole.class, description = "Role of the user in this specific project")
    private ProjectRole role;

    @Schema(description = "Timestamp when the user was added to the project (Read-only)")
    private LocalDateTime joinedAt;
}