package com.example.Project_Tracker.controller;

import com.example.Project_Tracker.dto.ApiResponse;
import com.example.Project_Tracker.dto.MilestoneDto;
import com.example.Project_Tracker.service.MilestoneService;

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
@RequestMapping("/api/milestones")
@RequiredArgsConstructor
// @CrossOrigin
@Tag(name = "Milestone Management", description = "Operations for managing milestones within a project (Manager/Employee scope).")
@SecurityRequirement(name = "bearer-jwt")
public class MilestoneController {

    private final MilestoneService milestoneService;

    // --- Examples ---
    private static final String EXAMPLE_MILESTONE_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Milestone updated successfully\",\"data\":{\"id\":\"m-5678\",\"name\":\"Backend API Finalized\",\"description\":\"Final review of all backend endpoints.\",\"status\":\"IN_PROGRESS\",\"startDate\":\"2024-01-15\",\"endDate\":\"2024-03-30\",\"projectId\":\"p-1234\",\"projectName\":\"Customer Portal Redesign\",\"dependsOnId\":null,\"dependsOnName\":null},\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_MILESTONE_LIST_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Milestones fetched successfully\",\"data\":[{\"id\":\"m-5678\",\"name\":\"Backend API Finalized\",\"status\":\"IN_PROGRESS\",\"startDate\":\"2024-01-15\",\"endDate\":\"2024-03-30\",\"projectId\":\"p-1234\"}],\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_ERROR_409_MILESTONE = "{\"statusCode\":409,\"success\":false,\"message\":\"Cannot start this milestone until the dependent one is completed\",\"data\":null,\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    // ---

    // ✅ Get milestones by project
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all milestones for a specific project",
        description = "Requires Manager or Project Manager role in the Project's IBU.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Milestones fetched successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_MILESTONE_LIST_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access Denied")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ApiResponse<List<MilestoneDto>>> getMilestonesByProject(@PathVariable String projectId) {
        List<MilestoneDto> milestones = milestoneService.getMilestonesByProject(projectId);

        ApiResponse<List<MilestoneDto>> response = ApiResponse.<List<MilestoneDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Milestones fetched successfully")
                .data(milestones)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
@GetMapping("/{milestoneId}")
    @Operation(summary = "Get all milestones for a specific project",
        description = "Requires Manager or Project Manager role in the Project's IBU.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Milestones fetched successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_MILESTONE_LIST_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access Denied")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ApiResponse<MilestoneDto>> getMilestonesById(@PathVariable String milestoneId) {
        MilestoneDto milestone = milestoneService.getMilestonesById(milestoneId);

        ApiResponse<MilestoneDto> response = ApiResponse.<MilestoneDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Milestone fetched successfully")
                .data(milestone)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
    // ✅ Create milestone
    @PostMapping
    @Operation(summary = "Create a new milestone",
        description = "Requires Manager or Project Manager role in the Project's IBU.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Milestone created successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_MILESTONE_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access Denied")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project or Dependency Milestone not found")
    public ResponseEntity<ApiResponse<MilestoneDto>> createMilestone(@Valid @RequestBody MilestoneDto milestoneDto) {
        MilestoneDto created = milestoneService.createMilestone(milestoneDto);

        ApiResponse<MilestoneDto> response = ApiResponse.<MilestoneDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .success(true)
                .message("Milestone created successfully")
                .data(created)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Update milestone
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing milestone",
        description = "Requires Manager or Project Manager role in the Project's IBU.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Milestone updated successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_MILESTONE_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access Denied")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Milestone or Project not found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Cannot start milestone until dependent milestone is completed",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_409_MILESTONE)))
    public ResponseEntity<ApiResponse<MilestoneDto>> updateMilestone( @Valid
            @PathVariable String id,
            @RequestBody MilestoneDto milestoneDto
    ) {
        MilestoneDto updated = milestoneService.updateMilestone(id, milestoneDto);

        ApiResponse<MilestoneDto> response = ApiResponse.<MilestoneDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Milestone updated successfully")
                .data(updated)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // ✅ Delete milestone
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a milestone",
        description = "Requires Manager or Project Manager role in the Project's IBU.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Milestone deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access Denied")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Milestone not found")
    public ResponseEntity<ApiResponse<String>> deleteMilestone(@PathVariable String id) {
        milestoneService.deleteMilestone(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Milestone deleted successfully")
                .data(id)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}