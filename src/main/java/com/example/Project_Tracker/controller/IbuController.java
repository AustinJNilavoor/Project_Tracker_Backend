package com.example.Project_Tracker.controller;

import com.example.Project_Tracker.dto.ApiResponse;
import com.example.Project_Tracker.dto.IbuDto;
import com.example.Project_Tracker.service.IbuService;

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
@RequestMapping("/api/ibus")
@RequiredArgsConstructor
// @CrossOrigin
@Tag(name = "IBU Management", description = "Operations for managing International Business Units (ADMIN only for CUD).")
@SecurityRequirement(name = "bearer-jwt")
public class IbuController {

    private final IbuService ibuService;

    // --- Examples ---
    private static final String EXAMPLE_IBU_LIST_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"Fetched all IBUs successfully\",\"data\":[{\"id\":\"i-100\",\"name\":\"Enterprise Solutions\",\"description\":\"Focuses on B2B software\"},{\"id\":\"i-200\",\"name\":\"Consumer Products\",\"description\":\"Focuses on mobile apps\"}],\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_IBU_CREATE_RESPONSE = "{\"statusCode\":201,\"success\":true,\"message\":\"IBU created successfully\",\"data\":{\"id\":\"i-300\",\"name\":\"R&D Division\",\"description\":\"New technology research\"},\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_IBU_DELETE_RESPONSE = "{\"statusCode\":200,\"success\":true,\"message\":\"IBU deleted successfully\",\"data\":\"i-300\",\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    private static final String EXAMPLE_ERROR_409_IBU = "{\"statusCode\":409,\"success\":false,\"message\":\"IBU linked to users or projects\",\"data\":null,\"timestamp\":\"2024-10-27T10:30:00.123456\"}";
    // ---

    // ✅ Get all IBUs
    @GetMapping
    @Operation(summary = "Get all IBUs",
        description = "Accessible to all authenticated users.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched all IBUs successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_IBU_LIST_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<ApiResponse<List<IbuDto>>> getAllIbus() {
        List<IbuDto> ibus = ibuService.getAllIbus();

        ApiResponse<List<IbuDto>> response = ApiResponse.<List<IbuDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("Fetched all IBUs successfully")
                .data(ibus)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // ✅ Create new IBU
    @PostMapping
    @Operation(summary = "Create a new IBU (ADMIN only)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "IBU created successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_IBU_CREATE_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Not ADMIN)")
    public ResponseEntity<ApiResponse<IbuDto>> createIbu(@Valid @RequestBody IbuDto ibuDto) {
        IbuDto newIbu = ibuService.createIbu(ibuDto);

        ApiResponse<IbuDto> response = ApiResponse.<IbuDto>builder()
                .statusCode(HttpStatus.CREATED.value())
                .success(true)
                .message("IBU created successfully")
                .data(newIbu)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ✅ Delete IBU by ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an IBU by ID (ADMIN only)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "IBU deleted successfully",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_IBU_DELETE_RESPONSE)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden (Not ADMIN)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "IBU not found")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "IBU linked to users or projects (Data Integrity)",
        content = @Content(mediaType = "application/json", examples = @ExampleObject(value = EXAMPLE_ERROR_409_IBU)))
    public ResponseEntity<ApiResponse<String>> deleteIbu(@PathVariable String id) {
        ibuService.deleteIbu(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .success(true)
                .message("IBU deleted successfully")
                .data(id)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}