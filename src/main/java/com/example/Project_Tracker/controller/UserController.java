package com.example.Project_Tracker.controller;

import com.example.Project_Tracker.dto.ApiResponse;
import com.example.Project_Tracker.dto.UserDto;
import com.example.Project_Tracker.service.UserService;
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

@RequestMapping("/api/users")
// @CrossOrigin
@RequiredArgsConstructor

@Tag(name = "User Management", description = "Operations for managing system users (Admin/Manager scope).")
@SecurityRequirement(name = "bearer-jwt")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all visible users",
        description = "Admin sees all users. Manager sees users in their IBU. Employee only sees themselves.")
    // @OApi(responseCode = "200", description = "Users fetched successfully")
    // @OApi(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        ApiResponse<List<UserDto>> response = ApiResponse.<List<UserDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Users fetched successfully")
                .data(users)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single user by ID")
    // @OApi(responseCode = "200", description = "User details fetched successfully")
    // @OApi(responseCode = "404", description = "User not found")
    public ResponseEntity<ApiResponse<UserDto>> getUser(@PathVariable String id) {
        UserDto user = userService.getUserById(id);

        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("User details fetched successfully")
                .data(user)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing user (Admin only)")
    // @OApi(responseCode = "200", description = "User updated successfully")
    // @OApi(responseCode = "403", description = "Forbidden (Not ADMIN)")
    // @OApi(responseCode = "404", description = "User or IBU not found")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable String id,
            @RequestBody UserDto user
    ) {
        UserDto updated = userService.updateUser(id, user);

        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("User updated successfully")
                .data(updated)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user (Admin only)")
    // @OApi(responseCode = "200", description = "User deleted successfully")
    // @OApi(responseCode = "403", description = "Forbidden (Not ADMIN)")
    // @OApi(responseCode = "404", description = "User not found")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("User deleted successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}