package com.example.Project_Tracker.controller;

import com.example.Project_Tracker.dto.ApiResponse;
import com.example.Project_Tracker.dto.TaskDto;
import com.example.Project_Tracker.enums.TaskStatus;
import com.example.Project_Tracker.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.responses.ApiResponse as OApi;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
// @CrossOrigin
@Tag(name = "Task Management", description = "Operations for managing project tasks (Employee/Manager scope).")
@SecurityRequirement(name = "bearer-jwt")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all tasks for a specific project")
//     @OApi(responseCode = "200", description = "Tasks fetched successfully")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getTasksByProject(@PathVariable String projectId) {
        List<TaskDto> tasks = taskService.getTasksByProject(projectId);

        ApiResponse<List<TaskDto>> response = ApiResponse.<List<TaskDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Tasks for project fetched successfully")
                .data(tasks)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all tasks assigned to a specific user")
//     @OApi(responseCode = "200", description = "Tasks fetched successfully")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getTasksByUser(@PathVariable String userId) {
        List<TaskDto> tasks = taskService.getTasksByUser(userId);

        ApiResponse<List<TaskDto>> response = ApiResponse.<List<TaskDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Tasks for user fetched successfully")
                .data(tasks)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/milestone/{milestoneId}")
    @Operation(summary = "Get all tasks belonging to a specific milestone")
//     @OApi(responseCode = "200", description = "Tasks fetched successfully")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getTasksByMilestone(@PathVariable String milestoneId) {
        List<TaskDto> tasks = taskService.getTasksByMilestone(milestoneId);

        ApiResponse<List<TaskDto>> response = ApiResponse.<List<TaskDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Tasks for milestone fetched successfully")
                .data(tasks)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new task (Manager/Project Manager only)",
        description = "The assigned user must be a member of the project.")
//     @OApi(responseCode = "201", description = "Task created successfully")
//     @OApi(responseCode = "403", description = "Access Denied")
//     @OApi(responseCode = "404", description = "Project or Milestone not found")
//     @OApi(responseCode = "409", description = "Assigned user is not a member of the project")
    public ResponseEntity<ApiResponse<TaskDto>> createTask(@Valid @RequestBody TaskDto taskDto) {
        TaskDto created = taskService.createTask(taskDto);

        ApiResponse<TaskDto> response = ApiResponse.<TaskDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .success(true)
                .message("Task created successfully")
                .data(created)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task (Manager/Project Manager only)")
//     @OApi(responseCode = "200", description = "Task updated successfully")
//     @OApi(responseCode = "403", description = "Access Denied")
//     @OApi(responseCode = "404", description = "Task, Project, or User not found")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(@Valid @PathVariable String id, @RequestBody TaskDto taskDto) {
        TaskDto updated = taskService.updateTask(id, taskDto);

        ApiResponse<TaskDto> response = ApiResponse.<TaskDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Task updated successfully")
                .data(updated)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update task status (Assigned User, Manager, or Project Manager only)")
//     @OApi(responseCode = "200", description = "Task status updated successfully")
//     @OApi(responseCode = "403", description = "Access Denied")
//     @OApi(responseCode = "404", description = "Task not found")
    public ResponseEntity<ApiResponse<TaskDto>> updateTaskStatus( @PathVariable String id,
            @RequestParam TaskStatus status) {
        TaskDto updated = taskService.updateTaskStatus(id, status);

        ApiResponse<TaskDto> response = ApiResponse.<TaskDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Task status updated successfully")
                .data(updated)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task (Manager/Project Manager only)")
//     @OApi(responseCode = "200", description = "Task deleted successfully")
//     @OApi(responseCode = "403", description = "Access Denied")
//     @OApi(responseCode = "404", description = "Task not found")
    public ResponseEntity<ApiResponse<String>> deleteTask(@PathVariable String id) {
        boolean deleted = taskService.deleteTask(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .statusCode(deleted ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value())
                .success(deleted)
                .message(deleted ? "Task deleted successfully" : "Task not found")
                .data(id)
                .timestamp(LocalDateTime.now())
                .build();

        return deleted ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}