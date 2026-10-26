package com.example.Project_Tracker.controller;

import com.example.Project_Tracker.dto.ApiResponse;
import com.example.Project_Tracker.dto.ProjectMemberDto;
import com.example.Project_Tracker.service.ProjectMemberService;

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
@RequestMapping("/api/members")
@RequiredArgsConstructor
// @CrossOrigin
@Tag(name = "Project Member Management", description = "Operations for managing user membership in projects (Manager/Project Manager scope).")
@SecurityRequirement(name = "bearer-jwt")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    // --- Examples ---
    private static final String EXAMPLE_MEMBER_LIST_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Project members fetched successfully\",\"data\":[{\"projectId\":\"p-1234\",\"projectName\":\"Portal Redesign\",\"userId\":\"u-9876\",\"userName\":\"Alice Smith\",\"role\":\"PROJECT_MANAGER\",\"joinedAt\":\"2024-01-01T10:00:00\"},{\"projectId\":\"p-1234\",\"projectName\":\"Portal Redesign\",\"userId\":\"u-5432\",\"userName\":\"Bob Johnson\",\"role\":\"DEVELOPER\",\"joinedAt\":\"2024-01-05T11:00:00\"}],\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_MEMBER_ADD_RESPONSE = "{\"statusCode\":201,\"success\":true,\"message\":\"Member added successfully\",\"data\":{\"projectId\":\"p-1234\",\"projectName\":\"Portal Redesign\",\"userId\":\"u-1111\",\"userName\":\"New Employee\",\"role\":\"TESTER\",\"joinedAt\":\"2024-10-27T10:30:00.123456\"},\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_MEMBER_REMOVE_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Member removed successfully\",\"data\":\"Removed member u-1111 from project p-1234\",\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_ERROR_409_MEMBER = "{\"statusCode\":409,\"success\":false,\"message\":\"The user (Jane Doe) is already a member of this project.\",\"data\":null,\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    // ---

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all members of a project")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Project members fetched successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_MEMBER_LIST_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<List<ProjectMemberDto>>> getMembersByProject(@PathVariable String projectId) {
        List<ProjectMemberDto> members = projectMemberService.getMembersByProject(projectId);

        ApiResponse<List<ProjectMemberDto>> response = ApiResponse.<List<ProjectMemberDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Project members fetched successfully")
                .data(members)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Add a user as a member to a project (Manager/Project Manager only)",
        description = "The user being added must belong to the same IBU as the project.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Member added successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_MEMBER_ADD_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access Denied (Not Manager/Project Manager or IBU mismatch)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Project or User not found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Member already exists",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_409_MEMBER)))
    public ResponseEntity<ApiResponse<ProjectMemberDto>> addMember(@Valid @RequestBody ProjectMemberDto dto) {
        ProjectMemberDto created = projectMemberService.addMember(dto);

        ApiResponse<ProjectMemberDto> response = ApiResponse.<ProjectMemberDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .success(true)
                .message("Member added successfully")
                .data(created)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   @DeleteMapping("/project/remove-member")
@Operation(summary = "Remove a member from a project (Manager/Project Manager only)")
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Member removed successfully",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = EXAMPLE_MEMBER_REMOVE_RESPONSE)
        )
)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "Access Denied (Not Manager/Project Manager or IBU mismatch)"
)
@io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Project or Project Member not found"
)
public ResponseEntity<ApiResponse<String>> removeMember(
        @RequestParam String projectId,
        @RequestParam String userId
) {

    projectMemberService.removeMember(projectId, userId);

    ApiResponse<String> response = ApiResponse.<String>builder()
            .statusCode(HttpStatus.OK.value())
            .success(true)
            .message("Member removed successfully")
            .data("Removed member " + userId + " from project " + projectId)
            .timestamp(LocalDateTime.now())
            .build();

    return ResponseEntity.ok(response);
}

}