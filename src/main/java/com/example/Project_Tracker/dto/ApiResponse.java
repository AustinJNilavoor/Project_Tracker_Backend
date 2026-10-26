package com.example.Project_Tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Standard API Response wrapper for all successful and error payloads.")
public class ApiResponse<T> {
    
    @Schema(description = "HTTP Status Code", example = "200")
    private int statusCode;
    
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;
    
    @Schema(description = "A brief message about the response", example = "Users fetched successfully")
    private String message;
    
    @Schema(description = "The main data payload of the response")
    private T data;
    
    @Schema(description = "Timestamp of the response generation", example = "2023-10-27T10:30:00.123456")
    private LocalDateTime timestamp;
}