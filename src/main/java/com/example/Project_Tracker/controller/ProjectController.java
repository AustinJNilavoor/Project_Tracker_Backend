package com.example.Project_Tracker.controller;

import com.example.Project_Tracker.dto.ApiResponse;
import com.example.Project_Tracker.dto.ProjectDto;
import com.example.Project_Tracker.dto.ProjectResponseDto;
import com.example.Project_Tracker.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
// @CrossOrigin
@Tag(name = "Project Management", description = "Operations for managing projects.")
@SecurityRequirement(name = "bearer-jwt")
public class ProjectController {

    private final ProjectService projectService;

    // --- Examples ---
    private static final String EXAMPLE_PROJECT_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Project fetched successfully\",\"data\":{\"id\":\"p-1234\",\"name\":\"Customer Portal Redesign\",\"description\":\"Redesign of the customer-facing web portal.\",\"status\":\"IN_PROGRESS\",\"priority\":\"HIGH\",\"startDate\":\"22024-01-01\",\"endDate\":\"2024-06-30\"},\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_PROJECT_LIST_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Projects fetched successfully\",\"data\":[{\"id\":\"p-1234\",\"name\":\"Customer Portal Redesign\",\"status\":\"IN_PROGRESS\"},{\"id\":\"p-5678\",\"name\":\"Mobile App Launch\",\"status\":\"PENDING\"}],\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_PROJECT_CREATE_RESPONSE = "{\"statusCode\":201,\"success\":true,\"message\":\"Project created successfully\",\"data\":{\"id\":\"p-9012\",\"name\":\"New HR System\",\"status\":\"PENDING\",\"priority\":\"MEDIUM\",\"startDate\":\"2024-11-01\",\"endDate\":\"2025-03-31\"},\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_PROJECT_DELETE_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Project deleted successfully\",\"data\":\"p-1234\",\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    // ---

    @GetMapping
    @Operation(summary = "Get all projects visible to the current user",
        description = "Admin sees all projects. Manager sees projects in their IBU. Employee sees projects they are a member of.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Projects fetched successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_PROJECT_LIST_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Manager not associated with any IBU")
    public ResponseEntity<ApiResponse<List<ProjectResponseDto>>> getAllProjects() {
        List<ProjectResponseDto> projects = projectService.getAllProjects();

        ApiResponse<List<ProjectResponseDto>> response = ApiResponse.<List<ProjectResponseDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Projects fetched successfully")
                .data(projects)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single project by ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project fetched successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_PROJECT_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ApiResponse<ProjectDto>> getProject(@PathVariable String id) {
        ProjectDto project = projectService.getProjectById(id);

        ApiResponse<ProjectDto> response = ApiResponse.<ProjectDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Project fetched successfully")
                .data(project)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new project (Manager/Admin only)",
        description = "The project will be automatically associated with the creator's IBU.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Project created successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_PROJECT_CREATE_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Not Manager/Admin)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "User not associated with any IBU")
    public ResponseEntity<ApiResponse<ProjectDto>> createProject(@Valid @RequestBody ProjectDto projectDto) {
        ProjectDto created = projectService.createProjectFromDto(projectDto);

        ApiResponse<ProjectDto> response = ApiResponse.<ProjectDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .success(true)
                .message("Project created successfully")
                .data(created)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing project (Manager/Admin only)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project updated successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_PROJECT_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Not Manager/Admin)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ApiResponse<ProjectDto>> updateProject( @Valid
            @PathVariable String id,
            @RequestBody ProjectDto projectDto
    ) {
        ProjectDto updated = projectService.updateProjectFromDto(id, projectDto);

        ApiResponse<ProjectDto> response = ApiResponse.<ProjectDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Project updated successfully")
                .data(updated)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project by ID (Manager/Admin only)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project deleted successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_PROJECT_DELETE_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Not Manager/Admin)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ApiResponse<String>> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Project deleted successfully")
                .data(id)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}