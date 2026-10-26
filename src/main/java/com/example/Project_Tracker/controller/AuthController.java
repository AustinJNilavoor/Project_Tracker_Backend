package com.example.Project_Tracker.controller;

import com.example.Project_Tracker.dto.ApiResponse;
import com.example.Project_Tracker.dto.AuthRequest;
import com.example.Project_Tracker.dto.AuthResponse;
import com.example.Project_Tracker.dto.ChangePasswordRequest;
import com.example.Project_Tracker.dto.RegisterRequest;
import com.example.Project_Tracker.dto.UserDto;
import com.example.Project_Tracker.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login operations.")
public class AuthController {

    private final AuthService authService;

    // --- Examples ---
    private static final String EXAMPLE_AUTH_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Login successful\",\"data\":{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"role\":\"MANAGER\",\"name\":\"Alice Smith\"},\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_ERROR_401 = "{\"statusCode\":401,\"success\":false,\"message\":\"Invalid credentials\",\"data\":null,\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_REGISTER_RESPONSE = "{\"statusCode\":201,\"success\":true,\"message\":\"User registered successfully\",\"data\":{\"id\":\"u-9876\",\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\",\"role\":\"EMPLOYEE\",\"ibuId\":\"i-100\",\"ibuName\":\"Enterprise Solutions\"},\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_ERROR_409 = "{\"statusCode\":409,\"success\":false,\"message\":\"User with email 'jane.doe@example.com' already exists\",\"data\":null,\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_ERROR_403 = "{\"statusCode\":403,\"success\":false,\"message\":\"Forbidden: Only ADMIN users can register new users\",\"data\":null,\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    // ---

    @PostMapping("/register")
    
    @Operation(summary = "Register a new user (ADMIN only)",
        description = "Requires an ADMIN token. Registers a new user with the specified role and IBU name.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully", 
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_REGISTER_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (Invalid Token)",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_401)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Not ADMIN)",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_403)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User/Email already exists or IBU not found",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_409)))
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto userDto = authService.register(request);

        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .statusCode(200)
                .success(true)
                .message("User registered successfully")
                .data(userDto)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and receive JWT token",
        description = "Public endpoint. Validates credentials and returns a JWT for subsequent requests.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_AUTH_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_401)))
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // New Endpoint for changing password
    @PutMapping("/password")
    @Operation(summary = "Change the password for the currently authenticated user",
        description = "Requires the old password for verification.")
    @SecurityRequirement(name = "bearer-jwt")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized (Invalid Token or Invalid Old Password)",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_401)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Password changed successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}